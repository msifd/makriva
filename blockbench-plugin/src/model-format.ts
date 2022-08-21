/// <reference types="./types/blockbench" />

export const format = new ModelFormat({
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