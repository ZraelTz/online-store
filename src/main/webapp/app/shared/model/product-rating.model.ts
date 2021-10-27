import { IProduct } from '@/shared/model/product.model';
import { IUser } from '@/shared/model/user.model';

export interface IProductRating {
  id?: number;
  value?: number;
  productId?: number;
  userId?: number;
  productRating?: IProduct;
  rating?: IUser;
}

export class ProductRating implements IProductRating {
  constructor(
    public id?: number,
    public value?: number,
    public productId?: number,
    public userId?: number,
    public productRating?: IProduct,
    public rating?: IUser
  ) {}
}
