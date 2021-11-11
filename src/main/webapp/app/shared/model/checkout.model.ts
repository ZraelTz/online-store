import { ICart } from '@/shared/model/cart.model';

export interface ICheckout {
  id?: number;
  quantity?: number;
  totalPrice?: number;
  cart?: ICart;
}

export class Checkout implements ICheckout {
  constructor(public id?: number, public quantity?: number, public totalPrice?: number, public cart?: ICart) {}
}
