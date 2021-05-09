(function () {
  const bipedParts = {
    "head": {
      bbPos: [0, 24, 0],
      mcPos: [-4, -8, -6],
      size: [8, 8, 8],
    },
    "body": {
      bbPos: [0, 24, 0],
      mcPos: [-4, 0, -2],
      size: [8, 12, 4],
    },
    "right_arm": {
      bbPos: [5, 22, 0],
      mcPos: [-3, -2, -2],
      size: [4, 12, 4],
    },
    "left_arm": {
      bbPos: [-5, 22, 0],
      mcPos: [-1, -2, -2],
      size: [4, 12, 4],
    },
    "right_leg": {
      bbPos: [1.9, 12, 0],
      mcPos: [-2, 0, -2],
      size: [4, 12, 4],
    },
    "left_leg": {
      bbPos: [-1.9, 12, 0],
      mcPos: [-2, 0, -2],
      size: [4, 12, 4],
    },
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
      console.log("compile");

      function isZeroed(arr) {
        return autoStringify(arr) === "[0, 0, 0]";
      }

      function skeletonOffset(group, name) {
        const original = bipedParts[name].bbPos;
        const actual = group.origin;
        return [
          original[0] - actual[0],
          original[1] - actual[1],
          actual[2] - original[2], // invert z coords
        ];
      }

      function convertCube(bb, bone, biped) {
        const size = [
          bb.to[0] - bb.from[0],
          bb.to[1] - bb.from[1],
          bb.to[2] - bb.from[2],
        ];

        const cube = {
          uv: bb.uv_offset,
          pos: [
            bb.parent.origin[0] - bb.from[0] - size[0],
            bb.parent.origin[1] - bb.from[1] - size[1],
            bb.parent.origin[2] - bb.from[2] - size[2],
          ],
          size: size,
        };

        console.log(bb.parent.name, cube.pos, bb.parent.origin, bb.from, size);

        if (bb.inflate != 0)
          cube.delta = bb.inflate;
        if (bb.mirror_uv)
          cube.mirror = true;

        return cube;
      }

      function convertBone(bb, bipedGroup, biped, parent) {
        const bone = {
          id: bb.name,
          parent: parent,
          offset: [
            bb.parent.origin[0] - bb.origin[0],
            bb.parent.origin[1] - bb.origin[1],
            bb.parent.origin[2] - bb.origin[2],
          ],
          rotation: bb.rotation,
          cubes: [],
          bones: [],
        };

        if (bb.children instanceof Array) {
          for (const child of bb.children) {
            if (!child.export) continue;

            if (child instanceof Cube) {
              bone.cubes.push(convertCube(child, bone, biped));
            } else if (child instanceof Group) {
              bone.bones.push(convertBone(child, bipedGroup, biped));
            }
          }
        }

        if (isZeroed(bone.offset)) bone.offset = undefined;
        if (isZeroed(bone.rotation)) bone.rotation = undefined;
        if (bone.cubes.length == 0) bone.cubes = undefined;
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
        if (!group.export) return;

        for (const child of group.children) {
          if (child instanceof Cube && child.name == "hide") {
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

        const offset = skeletonOffset(group, group.name);
        if (!isZeroed(offset))
          model.skeleton[group.name] = offset;
      });
      if (Object.keys(model.skeleton).length == 0) model.skeleton = undefined;

      // Bones
      Outliner.root.forEach(group => {
        if (!(group instanceof Group)) return;
        if (!(group.name in bipedParts)) return;
        if (!(group.children instanceof Array)) return;

        // Bind bones to biped parts
        for (const child of group.children) {
          if (!child.export) continue;
          if (!(child instanceof Group)) continue;

          // const offset = bipedOffset(child, group.name);
          const bone = convertBone(child, group, bipedParts[group.name], group.name);
          model.bones.push(bone);
        }
      });
      if (Object.keys(model.bones).length == 0) model.bones = undefined;

      return autoStringify(model);
    }
  });

  const export_action = new Action('makriva_export', {
    name: 'Export Makriva Shape',
    description: '',
    icon: 'icon-player',
    click: function () {
      exportOptions.skinUrl = "file:makriva/" + (ModelMeta.name || Project.name) + ".png";
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
      console.log(Outliner);
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