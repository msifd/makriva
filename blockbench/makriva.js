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
        return autoStringify(arr) === "[0, 0, 0]";
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

      function compileBone(bb, parent, attachmentPart) {
        const bone = {
          id: bb.name,
          parent: attachmentPart,
          offset: [
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

            if (child instanceof Cube) {
              if (isQuad(child))
                bone.quads.push(compileQuad(child, bb));
              else
                bone.cubes.push(compileCube(child, bb));
            } else if (child instanceof Group) {
              bone.bones.push(compileBone(child, bb));
            }
          }
        }

        if (isZeroed(bone.offset)) bone.offset = undefined;
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
      codec.export();

      // new Dialog({
      //     id: "makriva_export",
      //     title: "Makriva Export",
      //     form: {
      //         // ignore: { label: 'Ignore prefix', type: 'input', value: "_" },
      //         skinUrl: { label: 'Skin URL', type: 'input', value: exportOptions.skinUrl },
      //         slimModel: { label: 'Slim model', type: 'checkbox', value: exportOptions.slimModel },
      //         // hide: { label: 'hide', type: 'select', options: {a: "a", b: "b"} },
      //     },
      //     onConfirm: function (formData) {
      //         exportOptions = formData;
      //         codec.export();
      //         this.hide()
      //     }
      // }).show();
    }
  });

  const print_outliner = new Action('makriva_print', {
    name: 'Print outliner',
    description: '',
    icon: 'icon-player',
    click: function () {
      console.log(Outliner.root);
      console.log(ModelMeta);
      console.log(Project);
    }
  });

  Plugin.register('makriva', {
    title: 'Makriva plugin',
    author: 'msifeed',
    description: 'Export model to Makriva shape',
    icon: 'icon-player',
    version: '0.1.0',
    variant: 'both',
    onload() {
      MenuBar.addAction(export_action, 'file.export');
      MenuBar.addAction(export_action, 'filter');
      MenuBar.addAction(print_outliner, 'filter');
    },
    onunload() {
      export_action.delete();
      print_outliner.delete();
    }
  });

})();