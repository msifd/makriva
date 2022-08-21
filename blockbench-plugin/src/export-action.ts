/// <reference types="./types/blockbench" />
import { ExportOptions, exportOptions as opts, updateOptions } from "./export-options";
import { codec } from "./codec";

const createForm: () => Record<keyof ExportOptions, DialogFormElement> = () => ({
  skinUrl: { label: 'Skin URL', type: 'text', value: opts.skinUrl },
  slimModel: { label: 'Slim model', type: 'checkbox', value: opts.slimModel },

  hideHead: { label: 'Hide Head', type: 'checkbox', value: opts.hideHead },
  hideBody: { label: 'Hide Body', type: 'checkbox', value: opts.hideBody },
  hideRightArm: { label: 'Hide Right Arm', type: 'checkbox', value: opts.hideRightArm },
  hideLeftArm: { label: 'Hide Left Arm', type: 'checkbox', value: opts.hideLeftArm },
  hideRightLeg: { label: 'Hide Right Leg', type: 'checkbox', value: opts.hideRightLeg },
  hideLeftLeg: { label: 'Hide Left Leg', type: 'checkbox', value: opts.hideLeftLeg },

  boundingBoxJson: { label: 'boundingBox JSON', type: 'text', value: opts.boundingBoxJson },
  exprAnimationJson: { label: 'animation JSON', type: 'text', value: opts.exprAnimationJson },
});

let validationErrors: string[] = [];

function validateForm(this: Dialog & DialogOptions, formData: ExportOptions) {
  validationErrors = [];

  try {
    if (formData.boundingBoxJson) JSON.parse(formData.boundingBoxJson);
  } catch (e) {
    validationErrors.push("Invalid boundingBox JSON");
  }
  try {
    if (formData.exprAnimationJson) JSON.parse(formData.exprAnimationJson);
  } catch (e) {
    validationErrors.push("Invalid animation JSON");
  }
}

const exportAction = new Action('makriva_export', {
  name: 'Export Makriva Shape',
  category: 'file',
  icon: 'icon-player',
  click: function () {
    if (!Project) return;

    if (opts.skinUrl === "" && Project.textures.length > 0) {
      const tx = Project.textures[0] as TextureData;
      opts.skinUrl = "file:makriva/" + tx.name;
    }

    new Dialog({
      id: "makriva_export",
      title: "Makriva Shape Export",
      form: createForm(),
      onFormChange: validateForm,
      onConfirm: function (this: Dialog & DialogOptions, formData: ExportOptions) {
        updateOptions(formData as ExportOptions);

        if (validationErrors.length === 0) {
          codec.export!();
        } else {
          new Dialog({
            id: "makriva_export_error",
            title: "Invalid export parameters",
            singleButton: true,
            lines: validationErrors
          }).show();
        }
        // this.hide();
      }
    }).show();
  }
});

export const loadExportAction = () => {
  MenuBar.addAction(exportAction, 'file.export');
};

export const unloadExportAction = () => {
  exportAction.delete();
}