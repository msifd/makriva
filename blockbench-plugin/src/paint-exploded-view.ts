/// <reference types="./types/blockbench" />

const paintExplodedView = new Toggle('paint_exploded_view', {
  icon: () => 'open_in_full',
  category: 'edit',
  condition: { modes: ['paint'] },
  name: 'Exploded View (Makriva)',
  description: 'Toggles an exploded view that allows you to edit covered faces',
  value: false,
  onChange(exploded_view: boolean) {
    if (!Project) return;

    Undo.initEdit({ elements: Cube.all, exploded_view: !exploded_view });

    Cube.all.forEach((cube: BCube) => {
      const offset = cube.name.toLowerCase().includes('leg') ? 1 : 0.5;
      const legOffset = exploded_view ? offset : -offset / (1 + offset);
      const center: ArrayVector3 = [
        cube.from[0] + (cube.to[0] - cube.from[0]) / 2,
        cube.from[1],
        cube.from[2] + (cube.to[2] - cube.from[2]) / 2,
      ];

      center.V3_multiply(legOffset, legOffset, legOffset)
      cube.from.V3_add(center);
      cube.to.V3_add(center);
    });
    (Project as BProject).exploded_view = exploded_view;

    Undo.finishEdit(
      exploded_view ? 'Explode skin model' : 'Revert exploding skin model',
      { elements: Cube.all, exploded_view: exploded_view }
    );
    Canvas.updateView({ elements: Cube.all, element_aspects: { geometry: true } });
    this.setIcon(this.icon);
  }
});

export const loadPaintExplodedView = () => {
  Blockbench.on('select_project' as EventName, () => {
    if (!Project) return;

    paintExplodedView.value = (Project as BProject).exploded_view as boolean;
    paintExplodedView.updateEnabledState();
  });

  const alreadyInToolbar = Toolbars.outliner.children
    .some((action: BToolbar) => action.id === paintExplodedView.id)
  if (alreadyInToolbar) return;

  // Add paint_explode_model after explode_skin_model
  const explodeIndex = Toolbars.outliner.children
    .map((action: BToolbar) => action.id)
    .indexOf('explode_skin_model');

  if (explodeIndex !== -1)
    paintExplodedView.pushToolbar(Toolbars.outliner, explodeIndex);
  else
    paintExplodedView.pushToolbar(Toolbars.outliner);
};

export const unloadPaintExplodedView = () => {
  paintExplodedView.delete();
}