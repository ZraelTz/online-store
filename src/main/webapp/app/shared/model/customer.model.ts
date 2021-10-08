import { IUser } from '@/shared/model/user.model';
import { IProductOrder } from '@/shared/model/product-order.model';

import { Gender } from '@/shared/model/enumerations/gender.model';
export interface ICustomer {
  id?: number;
  firstName?: string;
  lastName?: string;
  gender?: Gender;
  email?: string;
  phone?: string;
  addressLine1?: string;
  addressLine2?: string | null;
  city?: string;
  countryState?: string;
  user?: IUser;
  orders?: IProductOrder[] | null;
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public firstName?: string,
    public lastName?: string,
    public gender?: Gender,
    public email?: string,
    public phone?: string,
    public addressLine1?: string,
    public addressLine2?: string | null,
    public city?: string,
    public countryState?: string,
    public user?: IUser,
    public orders?: IProductOrder[] | null
  ) {}
}
