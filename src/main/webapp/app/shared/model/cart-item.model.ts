import { IProduct } from '@/shared/model/product.model';
import { ICart } from '@/shared/model/cart.model';

export interface ICartItem {
  id?: number;
  quantity?: number;
  product?: IProduct;
  cart?: ICart | null;
}

export class CartItem implements ICartItem {
  constructor(public id?: number, public quantity?: number, public product?: IProduct, public cart?: ICart | null) {}
}
