export let exportOptions = {
  skinUrl: "",
  slimModel: false,
  hideHead: false,
  hideBody: false,
  hideLeftArm: false,
  hideRightArm: false,
  hideLeftLeg: false,
  hideRightLeg: false,
};

export const updateOptions = (opt: object) => {
  exportOptions = opt as typeof exportOptions;
};