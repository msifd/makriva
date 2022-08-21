import { version } from "../package.json";
import "./model-format";
import { loadExportAction, unloadExportAction } from "./export-action";
import { loadPaintExplodedView, unloadPaintExplodedView } from "./paint-exploded-view";

BBPlugin.register('makriva', {
  title: 'Makriva plugin',
  author: 'msifeed',
  description: 'Export model to Makriva shape',
  icon: 'icon-player',
  version,
  variant: 'both',
  onload: () => {
    loadExportAction();
    loadPaintExplodedView();
  },
  onunload: () => {
    unloadPaintExplodedView();
    unloadExportAction();
  }
} as PluginOptions);