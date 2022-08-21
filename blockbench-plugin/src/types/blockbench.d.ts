/// <reference types="blockbench-types" />

declare type BProject = ModelProject & {
  exploded_view: boolean;
} | null;
declare type BGroup = Group & GroupOptions;
declare type BCube = Cube & CubeOptions;
declare type BToolbar = Toolbar & { id: string };

declare class Toggle extends Action {
  readonly id: string;
  value: boolean;

  constructor(id, data);
  click();
  setIcon(icon);
  updateEnabledState();
  pushToolbar(bar: any, pos?: number);
}

declare const Toolbars: {
  outliner: {
    children: Toolbar[]
  };
};

declare function autoStringify(any): string;

declare type Array<T> = {
  V3_multiply(x: number, y?: number, z?: number): this
}