import { ICustomer } from '@/shared/model/customer.model';
import { ICheckout } from '@/shared/model/checkout.model';
import { ICartItem } from '@/shared/model/cart-item.model';

export interface ICart {
  id?: number;
  date?: Date;
  customer?: ICustomer;
  checkout?: ICheckout | null;
  cartItems?: ICartItem[] | null;
}

export class Cart implements ICart {
  constructor(
    public id?: number,
    public date?: Date,
    public customer?: ICustomer,
    public checkout?: ICheckout | null,
    public cartItems?: ICartItem[] | null
  ) {}
}
