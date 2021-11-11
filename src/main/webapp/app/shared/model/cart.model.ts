import { IUser } from '@/shared/model/user.model';
import { ICheckout } from '@/shared/model/checkout.model';
import { ICartItem } from '@/shared/model/cart-item.model';

export interface ICart {
  id?: number;
  date?: Date;
  user?: IUser;
  checkout?: ICheckout | null;
  cartItems?: ICartItem[] | null;
}

export class Cart implements ICart {
  constructor(
    public id?: number,
    public date?: Date,
    public user?: IUser,
    public checkout?: ICheckout | null,
    public cartItems?: ICartItem[] | null
  ) {}
}
