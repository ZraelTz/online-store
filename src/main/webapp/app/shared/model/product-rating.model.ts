import { IUser } from '@/shared/model/user.model';
import { IProduct } from '@/shared/model/product.model';

export interface IProductRating {
  id?: number;
  rating?: number;
  date?: Date;
  user?: IUser;
  product?: IProduct;
}

export class ProductRating implements IProductRating {
  constructor(public id?: number, public rating?: number, public date?: Date, public user?: IUser, public product?: IProduct) {}
}
