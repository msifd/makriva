export let exportOptions = {
  skinUrl: "",
  boundingBoxJson: "",
  exprAnimationJson: "",
  slimModel: false,
  hideHead: false,
  hideBody: false,
  hideLeftArm: false,
  hideRightArm: false,
  hideLeftLeg: false,
  hideRightLeg: false,
};

export type ExportOptions = typeof exportOptions;

export const updateOptions = (opt: ExportOptions) => {
  exportOptions = opt;
};