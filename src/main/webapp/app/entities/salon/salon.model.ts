import { IPacient } from 'app/entities/pacient/pacient.model';

export interface ISalon {
  id?: number;
  salonId?: number | null;
  sectieId?: number | null;
  salonIds?: IPacient[] | null;
}

export class Salon implements ISalon {
  constructor(public id?: number, public salonId?: number | null, public sectieId?: number | null, public salonIds?: IPacient[] | null) {}
}

export function getSalonIdentifier(salon: ISalon): number | undefined {
  return salon.id;
}
