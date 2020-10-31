import { IA } from 'app/shared/model/a.model';

export interface IB {
  id?: number;
  a?: IA;
}

export const defaultValue: Readonly<IB> = {};
