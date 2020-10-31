import { IB } from 'app/shared/model/b.model';

export interface IA {
  id?: number;
  bs?: IB[];
}

export const defaultValue: Readonly<IA> = {};
