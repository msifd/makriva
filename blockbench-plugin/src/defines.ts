import type { BipedPartName } from "./types/makriva.types";

export const bipedParts: Record<BipedPartName, [number, number, number]> = {
  head: [0, 24, 0],
  body: [0, 24, 0],
  left_arm: [-5, 22, 0],
  right_arm: [5, 22, 0],
  left_leg: [-1.9, 12, 0],
  right_leg: [1.9, 12, 0],
};