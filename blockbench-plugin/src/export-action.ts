/// <reference types="./types/blockbench" />
import { exportOptions, updateOptions } from "./export-options";
import { codec } from "./codec";

const exportAction = new Action('makriva_export', {
  name: 'Export Makriva Shape',
  category: 'file',
  icon: 'icon-player',
  click: function () {
    if (!Project) return;

    if (Project.textures.length > 0) {
      const tx = Project.textures[0] as TextureData;
      exportOptions.skinUrl = "file:makriva/" + tx.name;
    }

    new Dialog({
      id: "makriva_export",
      title: "Makriva Shape Export",
      form: {
        skinUrl: { label: 'Skin URL', type: 'text', value: exportOptions.skinUrl },
        slimModel: { label: 'Slim model', type: 'checkbox', value: exportOptions.slimModel },

        hideHead: { label: 'Hide Head', type: 'checkbox', value: exportOptions.hideHead },
        hideBody: { label: 'Hide Body', type: 'checkbox', value: exportOptions.hideBody },
        hideRightArm: { label: 'Hide Right Arm', type: 'checkbox', value: exportOptions.hideRightArm },
        hideLeftArm: { label: 'Hide Left Arm', type: 'checkbox', value: exportOptions.hideLeftArm },
        hideRightLeg: { label: 'Hide Right Leg', type: 'checkbox', value: exportOptions.hideRightLeg },
        hideLeftLeg: { label: 'Hide Left Leg', type: 'checkbox', value: exportOptions.hideLeftLeg },
      },
      onConfirm: function (this: Dialog & DialogOptions, formData) {
        updateOptions(formData);
        codec.export!();
        this.hide()
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