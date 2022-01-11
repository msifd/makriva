(function () {
  const bipedParts = {
    "head": [0, 24, 0],
    "body": [0, 24, 0],
    "right_arm": [5, 22, 0],
    "left_arm": [-5, 22, 0],
    "right_leg": [1.9, 12, 0],
    "left_leg": [-1.9, 12, 0],
  };

  let exportOptions = {
    skinUrl: "file:makriva/cisca.png",
    slimModel: false,
  };

  const codec = new Codec('makriva_shape', {
    name: "Makriva shape",
    extension: "json",
    remember: false,
    compile() {
      function isZeroed(arr) {
        return arr[0] == 0 && arr[1] == 0 && arr[2] == 0;
      }

      function isQuad(bb) {
        return bb.to[0] == bb.from[0] || bb.to[1] == bb.from[1] || bb.to[2] == bb.from[2];
      }

      function compileCube(bb, parent) {
        const cube = {
          uv: bb.uv_offset,
          pos: [
            parent.origin[0] - bb.to[0],
            parent.origin[1] - bb.to[1],
            bb.from[2] - parent.origin[2], // Invert Z
          ],
          size: [
            bb.to[0] - bb.from[0],
            bb.to[1] - bb.from[1],
            bb.to[2] - bb.from[2],
          ],
        };

        if (bb.inflate != 0)
          cube.delta = bb.inflate;
        if (bb.mirror_uv)
          cube.mirror = true;

        return cube;
      }

      function compileQuad(bb, parent) {
        const quad = {
          uv: [
            bb.uv_offset[0] + (bb.to[1] == bb.from[1] ? bb.to[2] - bb.from[2] : 0),
            bb.uv_offset[1] + (bb.to[0] == bb.from[0] ? bb.to[2] - bb.from[2] : 0),
          ],
          pos: [
            parent.origin[0] - bb.to[0],
            parent.origin[1] - bb.to[1],
            bb.from[2] - parent.origin[2], // Invert Z
          ],
          size: [
            bb.to[0] - bb.from[0],
            bb.to[1] - bb.from[1],
            bb.to[2] - bb.from[2],
          ],
        };

        if (bb.inflate != 0)
          quad.delta = bb.inflate;
        if (bb.mirror_uv)
          quad.mirror = true;

        return quad;
      }

      function compileWrap(bb, parent) {
        const bone = {
          id: bb.name + "_wrap",
          rotationPoint: [
            parent.origin[0] - bb.origin[0],
            parent.origin[1] - bb.origin[1],
            bb.origin[2] - parent.origin[2], // Invert Z
          ],
          rotation: [
            -bb.rotation[0],
            -bb.rotation[1],
            bb.rotation[2], // Invert Z
          ],
          cubes: [],
          quads: [],
        };

        if (isQuad(bb))
          bone.quads.push(compileQuad(bb, parent));
        else
          bone.cubes.push(compileCube(bb, parent));

        const inner = isQuad(bb) ? bone.quads[0] : bone.cubes[0];
        inner.pos[0] -= bone.rotationPoint[0];
        inner.pos[1] -= bone.rotationPoint[1];
        inner.pos[2] -= bone.rotationPoint[2];

        if (isZeroed(bone.rotationPoint)) bone.rotationPoint = undefined;
        if (bone.cubes.length == 0) bone.cubes = undefined;
        if (bone.quads.length == 0) bone.quads = undefined;

        return bone;
      }

      function compileBone(bb, parent, attachmentPart) {
        const bone = {
          id: bb.name,
          parent: attachmentPart,
          rotationPoint: [
            parent.origin[0] - bb.origin[0],
            parent.origin[1] - bb.origin[1],
            bb.origin[2] - parent.origin[2], // Invert Z
          ],
          rotation: [
            -bb.rotation[0],
            -bb.rotation[1],
            bb.rotation[2], // Invert Z
          ],
          cubes: [],
          quads: [],
          bones: [],
        };

        if (bb.children instanceof Array) {
          for (const child of bb.children) {
            if (!child.export) continue;

            if (child instanceof Group) {
              bone.bones.push(compileBone(child, bb));
            } else if (!isZeroed(child.rotation)) {
              bone.bones.push(compileWrap(child, bb));
            } else if (isQuad(child)) {
              bone.quads.push(compileQuad(child, bb));
            } else {
              bone.cubes.push(compileCube(child, bb));
            }
          }
        }

        if (isZeroed(bone.rotationPoint)) bone.rotationPoint = undefined;
        if (isZeroed(bone.rotation)) bone.rotation = undefined;
        if (bone.cubes.length == 0) bone.cubes = undefined;
        if (bone.quads.length == 0) bone.quads = undefined;
        if (bone.bones.length == 0) bone.bones = undefined;

        return bone;
      }

      // // // //

      const model = {
        metadata: {},
        textures: {
          skin: exportOptions.skinUrl,
        },
        hide: [],
        skeleton: {},
        bones: [],
      };

      // Metadata
      if (exportOptions.slimModel) model.metadata.model = "slim";

      // Hide
      Outliner.root.forEach(group => {
        if (!(group instanceof Group)) return;
        if (!(group.name in bipedParts)) return;

        for (const child of group.children) {
          if (!child.export || !(child instanceof Cube)) continue;
          if (child.name == "_hide") {
            model.hide.push(group.name);
            return;
          }
        }
      });

      // Skeleton
      Outliner.root.forEach(group => {
        if (!(group instanceof Group)) return;
        if (!(group.name in bipedParts)) return;
        if (!group.export) return;

        const part = bipedParts[group.name];
        const offset = [
          part[0] - group.origin[0],
          part[1] - group.origin[1],
          group.origin[2] - part[2], // Invert Z
        ];
        if (!isZeroed(offset))
          model.skeleton[group.name] = offset;
      });

      // Bones
      Outliner.root.forEach(group => {
        if (!(group instanceof Group)) return;
        if (!(group.name in bipedParts)) return;
        if (!(group.children instanceof Array)) return;

        const attachmentPart = {
          origin: [
            group.origin[0],
            group.origin[1],
            group.origin[2],
          ],
        };

        // Bind bones to biped parts
        for (const child of group.children) {
          if (!child.export) continue;
          if (!(child instanceof Group)) continue;
          model.bones.push(compileBone(child, attachmentPart, group.name));
        }
      });

      if (Object.keys(model.skeleton).length == 0) model.skeleton = undefined;
      if (Object.keys(model.bones).length == 0) model.bones = undefined;

      return autoStringify(model);
    }
  });

  const export_action = new Action('makriva_export', {
    name: 'Export Makriva Shape',
    description: '',
    icon: 'icon-player',
    click: function () {
      exportOptions.skinUrl = "file:makriva/" + Project.name + ".png";

      new Dialog({
          id: "makriva_export",
          title: "Makriva Export",
          form: {
              skinUrl: { label: 'Skin URL', type: 'input', value: exportOptions.skinUrl },
              slimModel: { label: 'Slim model', type: 'checkbox', value: exportOptions.slimModel },
          },
          onConfirm: function (formData) {
              exportOptions = formData;
              codec.export();
              this.hide()
          }
      }).show();
    }
  });

  Plugin.register('makriva', {
    title: 'Makriva plugin',
    author: 'msifeed',
    description: 'Export model to Makriva shape',
    icon: 'icon-player',
    version: '0.2.0',
    variant: 'both',
    onload() {
      MenuBar.addAction(export_action, 'file.export');
    },
    onunload() {
      export_action.delete();
    }
  });

})();