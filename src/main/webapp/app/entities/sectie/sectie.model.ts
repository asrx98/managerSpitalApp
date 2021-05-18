import { IPacient } from 'app/entities/pacient/pacient.model';
import { IPersonal } from 'app/entities/personal/personal.model';
import { TagSectie } from 'app/entities/enumerations/tag-sectie.model';

export interface ISectie {
  id?: number;
  sectieId?: number;
  nume?: string;
  sefId?: string;
  tag?: TagSectie;
  nrPaturi?: number;
  sectieIds?: IPacient[] | null;
  sectieIds?: IPersonal[] | null;
  sefId?: IPersonal | null;
}

export class Sectie implements ISectie {
  constructor(
    public id?: number,
    public sectieId?: number,
    public nume?: string,
    public sefId?: string,
    public tag?: TagSectie,
    public nrPaturi?: number,
    public sectieIds?: IPacient[] | null,
    public sectieIds?: IPersonal[] | null,
    public sefId?: IPersonal | null
  ) {}
}

export function getSectieIdentifier(sectie: ISectie): number | undefined {
  return sectie.id;
}
