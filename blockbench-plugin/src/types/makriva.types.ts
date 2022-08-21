export const bipedPartsNames = [
  "head", "body", "left_arm", "right_arm", "left_leg", "right_leg"
] as const;
export type BipedPartName = typeof bipedPartsNames[number];

export const poseNames = [
  "stand", "sneak", "sit", "sleep", "crawl", "elytraFly"
] as const;
export type PoseName = typeof poseNames[number];

export interface Shape {
  metadata: Metadata,
  textures: { [key: string]: string; },
  textureSize: [number, number],
  hide: BipedPartName[],
  skeleton: Partial<Record<BipedPartName, [number, number, number]>>,
  boundingBox: Partial<Record<PoseName, [number, number, number]>>,
  animation: ExprAnimation,
  bones: Bone[],
}

export interface Metadata {
  debug?: boolean,
  model?: "slim"
}

export interface ExprAnimation {
  [key: string]: ExprAnimation | [number, number, number];
}

export interface Bone {
  id: string,
  parent?: string,
  rotation?: [number, number, number];
  rotationPoint?: [number, number, number];
  cubes?: Cube[],
  quads?: Quad[],
  bones?: Bone[],
}

export interface Cube {
  pos: [number, number, number],
  size: [number, number, number],
  uv: [number, number],
  delta?: number,
  mirror?: boolean,
}

export interface Quad {
  pos: [number, number, number],
  size: [number, number, number],
  uv: [number, number],
  delta?: number,
  mirror?: boolean,
}