import { ICart } from '@/shared/model/cart.model';
import { IProduct } from '@/shared/model/product.model';

export interface ICartItem {
  id?: number;
  quantity?: number;
  cart?: ICart | null;
  product?: IProduct;
}

export class CartItem implements ICartItem {
  constructor(public id?: number, public quantity?: number, public cart?: ICart | null, public product?: IProduct) {}
}
