import { TagInventar } from 'app/entities/enumerations/tag-inventar.model';

export interface IInventar {
  id?: number;
  inventarId?: number;
  nume?: string;
  stoc?: number;
  tag?: TagInventar;
}

export class Inventar implements IInventar {
  constructor(public id?: number, public inventarId?: number, public nume?: string, public stoc?: number, public tag?: TagInventar) {}
}

export function getInventarIdentifier(inventar: IInventar): number | undefined {
  return inventar.id;
}
