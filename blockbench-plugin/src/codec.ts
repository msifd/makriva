/// <reference types="./types/blockbench" />
import type { BipedPartName } from "./types/makriva.types";
import type * as Makriva from "./types/makriva.types";

import { exportOptions as opts } from "./export-options";
import { bipedParts } from "./defines";

interface ParentNode {
  origin: [number, number, number]
}

const isZeroed = (arr: [number, number, number] | undefined): boolean => !arr || arr[0] == 0 && arr[1] == 0 && arr[2] == 0;
const areSame = (a: number, b: number): boolean => Math.abs((a - b) / b) < 0.000001;
const isQuad = (c: BCube) => areSame(c.to[0], c.from[0]) || areSame(c.to[1], c.from[1]) || areSame(c.to[2], c.from[2]);

function compileCube(bb: BCube, parent: ParentNode): Makriva.Cube {
  return {
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
    uv: bb.uv_offset,
    delta: bb.inflate !== 0 ? bb.inflate : undefined,
    mirror: bb.mirror_uv ? true : undefined
  };
}

function compileFlatCube(bb: BCube, parent: ParentNode): Makriva.Quad {
  return {
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
    uv: [
      bb.uv_offset[0] + (bb.to[1] == bb.from[1] ? bb.to[2] - bb.from[2] : 0),
      bb.uv_offset[1] + (bb.to[0] == bb.from[0] ? bb.to[2] - bb.from[2] : 0),
    ],
    delta: bb.inflate !== 0 ? bb.inflate : undefined,
    mirror: bb.mirror_uv ? true : undefined
  };
}

function compileWrap(bb: BCube, parent: ParentNode, attachmentPart?: BipedPartName): Makriva.Bone {
  const bone: Makriva.Bone = {
    id: bb.name,
    parent: attachmentPart,
    rotation: [
      -bb.rotation[0],
      -bb.rotation[1],
      bb.rotation[2], // Invert Z
    ],
    rotationPoint: [
      parent.origin[0] - bb.origin[0],
      parent.origin[1] - bb.origin[1],
      bb.origin[2] - parent.origin[2], // Invert Z
    ],
  };

  const offsetRotationPoint = (cq: Makriva.Cube | Makriva.Quad) => {
    cq.pos[0] -= bone.rotationPoint![0];
    cq.pos[1] -= bone.rotationPoint![1];
    cq.pos[2] -= bone.rotationPoint![2];
  };

  if (isQuad(bb)) {
    const q = compileFlatCube(bb, parent);
    offsetRotationPoint(q);
    bone.quads = [q];
  } else {
    const c = compileCube(bb, parent);
    offsetRotationPoint(c);
    bone.cubes = [c];
  }

  if (isZeroed(bone.rotationPoint!)) bone.rotationPoint = undefined;

  return bone;
}

function compileBone(bb: BGroup, parent: ParentNode, attachmentPart?: BipedPartName): Makriva.Bone {
  const bone: Makriva.Bone = {
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

  for (const child of Object.values(bb.children)) {
    if (!child.export) continue;

    if (child instanceof Group) {
      bone.bones!.push(compileBone(child as BGroup, bb));
      continue;
    }

    const cube = child as BCube;
    if (!isZeroed(cube.rotation)) {
      bone.bones!.push(compileWrap(cube, bb));
    } else if (isQuad(cube)) {
      bone.quads!.push(compileFlatCube(cube, bb));
    } else {
      bone.cubes!.push(compileCube(cube, bb));
    }
  }

  if (isZeroed(bone.rotationPoint)) bone.rotationPoint = undefined;
  if (isZeroed(bone.rotation)) bone.rotation = undefined;
  if (bone.cubes!.length == 0) bone.cubes = undefined;
  if (bone.quads!.length == 0) bone.quads = undefined;
  if (bone.bones!.length == 0) bone.bones = undefined;

  return bone;
}

function collectSkeletonOffsets() {
  const acc = {} as Record<BipedPartName, [number, number, number]>;

  for (const group of Object.values(Outliner.root)) {
    if (!(group instanceof Group)) continue;
    if (!(group.name in bipedParts)) continue;
    if (!group.export) continue;

    const bGroup = group as BGroup;
    const partName = group.name as BipedPartName;

    const part = bipedParts[partName];
    const offset: [number, number, number] = [
      part[0] - bGroup.origin[0],
      part[1] - bGroup.origin[1],
      bGroup.origin[2] - part[2], // Invert Z
    ];

    if (!isZeroed(offset)) acc[partName] = offset;
  }

  return acc;
}

function compile(this: Codec) {
  if (!Project) return "{}";

  const shape: Makriva.Shape = {
    metadata: {},
    textures: {
      skin: opts.skinUrl,
    },
    textureSize: [
      Project.texture_width,
      Project.texture_height
    ],
    hide: [],
    skeleton: collectSkeletonOffsets(),
    boundingBox: opts.boundingBoxJson ? JSON.parse(opts.boundingBoxJson) : undefined,
    animation: opts.exprAnimationJson ? JSON.parse(opts.exprAnimationJson) : undefined,
    bones: [],
  };

  // Metadata
  if (opts.slimModel) shape.metadata.model = "slim";

  // Hide
  if (opts.hideHead) shape.hide.push("head");
  if (opts.hideBody) shape.hide.push("body");
  if (opts.hideLeftArm) shape.hide.push("left_arm");
  if (opts.hideRightArm) shape.hide.push("right_arm");
  if (opts.hideLeftLeg) shape.hide.push("left_leg");
  if (opts.hideRightLeg) shape.hide.push("right_leg");

  // Skeleton

  // Bones
  for (const group of Object.values(Outliner.root)) {
    if (!(group instanceof Group)) continue;
    if (!(group.name in bipedParts)) continue;
    if (!(group.children instanceof Array)) continue;
    if (!(group.export)) continue;

    // Compile root bones and cubes

    const bGroup = group as BGroup;
    const partName = group.name as BipedPartName;
    const rootParent: ParentNode = {
      origin: [bGroup.origin[0], bGroup.origin[1], bGroup.origin[2]],
    };

    // Bind level-1 bones and cubes to biped parts
    for (const child of Object.values(group.children)) {
      if (!child.export) continue;

      if (child instanceof Group) {
        shape.bones.push(compileBone(child as BGroup, rootParent, partName));
      } else if (child instanceof Cube) {
        shape.bones.push(compileWrap(child as BCube, rootParent, partName));
      }
    }
  };

  return autoStringify(shape);
}

export const codec = new Codec('makriva', {
  name: "Makriva shape",
  extension: "json",
  remember: false,
  compile,
});