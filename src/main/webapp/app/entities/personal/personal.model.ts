import { ISectie } from 'app/entities/sectie/sectie.model';
import { IPacient } from 'app/entities/pacient/pacient.model';
import { TagPersonal } from 'app/entities/enumerations/tag-personal.model';

export interface IPersonal {
  id?: number;
  pacientId?: number;
  nume?: string;
  prenume?: string;
  sectieId?: string;
  tag?: TagPersonal;
  personalId?: ISectie | null;
  personalIds?: IPacient[] | null;
  sectie?: ISectie | null;
}

export class Personal implements IPersonal {
  constructor(
    public id?: number,
    public pacientId?: number,
    public nume?: string,
    public prenume?: string,
    public sectieId?: string,
    public tag?: TagPersonal,
    public personalId?: ISectie | null,
    public personalIds?: IPacient[] | null,
    public sectie?: ISectie | null
  ) {}
}

export function getPersonalIdentifier(personal: IPersonal): number | undefined {
  return personal.id;
}
