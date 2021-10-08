import { IUser } from '@/shared/model/user.model';

import { Gender } from '@/shared/model/enumerations/gender.model';
export interface IVendor {
  id?: number;
  firstName?: string;
  lastName?: string;
  gender?: Gender;
  email?: string;
  phone?: string;
  phone2?: string | null;
  addressLine1?: string;
  addressLine2?: string | null;
  city?: string;
  country?: string;
  storeName?: string;
  businessAccountNumber?: number;
  user?: IUser;
}

export class Vendor implements IVendor {
  constructor(
    public id?: number,
    public firstName?: string,
    public lastName?: string,
    public gender?: Gender,
    public email?: string,
    public phone?: string,
    public phone2?: string | null,
    public addressLine1?: string,
    public addressLine2?: string | null,
    public city?: string,
    public country?: string,
    public storeName?: string,
    public businessAccountNumber?: number,
    public user?: IUser
  ) {}
}
