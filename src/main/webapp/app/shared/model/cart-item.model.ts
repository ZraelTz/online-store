import { IProduct } from '@/shared/model/product.model';
import { ICustomer } from '@/shared/model/customer.model';
import { ICart } from '@/shared/model/cart.model';

export interface ICartItem {
  id?: number;
  quantity?: number;
  product?: IProduct;
  customer?: ICustomer;
  cart?: ICart | null;
}

export class CartItem implements ICartItem {
  constructor(
    public id?: number,
    public quantity?: number,
    public product?: IProduct,
    public customer?: ICustomer,
    public cart?: ICart | null
  ) {}
}
