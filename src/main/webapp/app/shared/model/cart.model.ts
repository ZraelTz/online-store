import { IUser } from '@/shared/model/user.model';
import { ICartItem } from '@/shared/model/cart-item.model';

export interface ICart {
  id?: number;
  quantity?: number;
  date?: Date;
  user?: IUser;
  items?: ICartItem[];
}

export class Cart implements ICart {
  constructor(public id?: number, public quantity?: number, public date?: Date, public user?: IUser, public items?: ICartItem[]) {}
}
