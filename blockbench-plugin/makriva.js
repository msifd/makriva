(function () {
  const bipedParts = {
    "head": [0, 24, 0],
    "body": [0, 24, 0],
    "left_arm": [-5, 22, 0],
    "right_arm": [5, 22, 0],
    "left_leg": [-1.9, 12, 0],
    "right_leg": [1.9, 12, 0],
  };

  let export_action;
  let paint_explode_model;

  function init() {
    let exportOptions = {
      skinUrl: "file:makriva/cisca.png",
      slimModel: false,
      hideHead: false,
      hideBody: false,
      hideLeftArm: false,
      hideRightArm: false,
      hideLeftLeg: false,
      hideRightLeg: false,
    };

    export_action = new Action('makriva_export', {
      name: 'Export Makriva Shape',
      category: 'file',
      icon: 'icon-player',
      click: function () {
        exportOptions.skinUrl = "file:makriva/" + Project.name + ".png";

        new Dialog({
            id: "makriva_export",
            title: "Makriva Shape Export",
            form: {
                skinUrl: { label: 'Skin URL', type: 'input', value: exportOptions.skinUrl },
                slimModel: { label: 'Slim model', type: 'checkbox', value: exportOptions.slimModel },

                hideHead: { label: 'Hide Head', type: 'checkbox', value: exportOptions.hideHead },
                hideBody: { label: 'Hide Body', type: 'checkbox', value: exportOptions.hideBody },
                hideRightArm: { label: 'Hide Right Arm', type: 'checkbox', value: exportOptions.hideRightArm },
                hideLeftArm: { label: 'Hide Left Arm', type: 'checkbox', value: exportOptions.hideLeftArm },
                hideRightLeg: { label: 'Hide Right Leg', type: 'checkbox', value: exportOptions.hideRightLeg },
                hideLeftLeg: { label: 'Hide Left Leg', type: 'checkbox', value: exportOptions.hideLeftLeg },
            },
            onConfirm: function (formData) {
                exportOptions = formData;
                codec.export();
                this.hide()
            }
        }).show();
      }
    });

    const codec = new Codec('makriva', {
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

        function compileWrap(bb, parent, attachmentPart) {
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
          };

          if (isQuad(bb))
            bone.quads.push(compileQuad(bb, parent));
          else
            bone.cubes.push(compileCube(bb, parent));

          // Offset inner cube
          const inner = isQuad(bb) ? bone.quads[0] : bone.cubes[0];
          inner.pos[0] -= bone.rotationPoint[0];
          inner.pos[1] -= bone.rotationPoint[1];
          inner.pos[2] -= bone.rotationPoint[2];

          if (isZeroed(bone.rotationPoint)) bone.rotationPoint = undefined;
          // if (isZeroed(bone.rotation)) bone.rotation = undefined;
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
          textureSize: [
            Project.texture_width,
            Project.texture_height
          ],
          hide: [],
          skeleton: {},
          bones: [],
        };

        // Metadata
        if (exportOptions.slimModel) model.metadata.model = "slim";

        // Hide
        if (exportOptions.hideHead) model.hide.push("head");
        if (exportOptions.hideBody) model.hide.push("body");
        if (exportOptions.hideLeftArm) model.hide.push("left_arm");
        if (exportOptions.hideRightArm) model.hide.push("right_arm");
        if (exportOptions.hideLeftLeg) model.hide.push("left_leg");
        if (exportOptions.hideRightLeg) model.hide.push("right_leg");

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
          if (!(group.export)) return;


          // Compile root bones and cubes
          const rootParent = {
            is_root: true,
            origin: [group.origin[0], group.origin[1], group.origin[2]],
          };

          // Bind level-1 bones and cubes to biped parts
          for (const child of group.children) {
            if (!child.export) continue;

            if (child instanceof Group) {
              model.bones.push(compileBone(child, rootParent, group.name));
            } else if (child instanceof Cube) {
              model.bones.push(compileWrap(child, rootParent, group.name));
            }
          }
        });

        if (Object.keys(model.skeleton).length == 0) model.skeleton = undefined;
        if (Object.keys(model.bones).length == 0) model.bones = undefined;

        return autoStringify(model);
      }
    });

    const format = new ModelFormat({
        id: "makriva_shape",
        name: "Makriva Shape",
        description: "Model for Makriva mod",
        icon: "icon-player",
        rotate_cubes: true,
        box_uv: true,
        optional_box_uv: false,
        single_texture: true,
        bone_rig: true,
        centered_grid: true,
        locators: true,
        display_mode: false,
        animation_mode: true,
        codec: Codecs.project,
    });

    paint_explode_model = new Toggle('paint_explode_model', {
      icon: () => 'open_in_full',
      category: 'edit',
      condition: {modes: ['paint']},
      name: 'Explode Model (Makriva)',
      description: 'Toggles an explosion view that allows you to edit covered faces',
      value: false,
      onChange(exploded_view) {
        Undo.initEdit({elements: Cube.all, exploded_view: !exploded_view});
        Cube.all.forEach(cube => {
          let center = [
            cube.from[0] + (cube.to[0] - cube.from[0]) / 2,
            cube.from[1],
            cube.from[2] + (cube.to[2] - cube.from[2]) / 2,
          ]
          let offset = cube.name.toLowerCase().includes('leg') ? 1 : 0.5;
          center.V3_multiply(exploded_view ? offset : -offset/(1+offset));
          cube.from.V3_add(center);
          cube.to.V3_add(center);
        })
        Project.exploded_view = exploded_view;
        Undo.finishEdit(exploded_view ? 'Explode skin model' : 'Revert exploding skin model', {elements: Cube.all, exploded_view: exploded_view});
        Canvas.updateView({elements: Cube.all, element_aspects: {geometry: true}});
        this.setIcon(this.icon);
      }
    });
    Blockbench.on('select_project', () => {
      paint_explode_model.value = !!Project.exploded_view;
      paint_explode_model.updateEnabledState();
    });
  }

  Plugin.register('makriva', {
    title: 'Makriva plugin',
    author: 'msifeed',
    description: 'Export model to Makriva shape',
    icon: 'icon-player',
    version: '0.2.1',
    variant: 'both',
    onload() {
      init();
      MenuBar.addAction(export_action, 'file.export');

      // Add paint_explode_model after explode_skin_model
      const explodeIndex = Toolbars.outliner.children
        .map(action => action.id)
        .indexOf('explode_skin_model');
      if (explodeIndex !== -1)
        paint_explode_model.pushToolbar(Toolbars.outliner, explodeIndex);
      else
        paint_explode_model.pushToolbar(Toolbars.outliner);
    },
    onunload() {
      export_action.delete();
      paint_explode_model.delete();
    }
  });

})();