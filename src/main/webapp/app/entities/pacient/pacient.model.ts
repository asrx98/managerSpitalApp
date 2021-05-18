import { IPersonal } from 'app/entities/personal/personal.model';
import { ISectie } from 'app/entities/sectie/sectie.model';
import { ISalon } from 'app/entities/salon/salon.model';

export interface IPacient {
  id?: number;
  pacientId?: number;
  nume?: string;
  prenume?: string;
  sectieId?: string;
  salonId?: string;
  personal?: IPersonal | null;
  sectie?: ISectie | null;
  salon?: ISalon | null;
}

export class Pacient implements IPacient {
  constructor(
    public id?: number,
    public pacientId?: number,
    public nume?: string,
    public prenume?: string,
    public sectieId?: string,
    public salonId?: string,
    public personal?: IPersonal | null,
    public sectie?: ISectie | null,
    public salon?: ISalon | null
  ) {}
}

export function getPacientIdentifier(pacient: IPacient): number | undefined {
  return pacient.id;
}
