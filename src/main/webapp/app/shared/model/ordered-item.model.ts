import { IProduct } from '@/shared/model/product.model';
import { IProductOrder } from '@/shared/model/product-order.model';

import { OrderedItemStatus } from '@/shared/model/enumerations/ordered-item-status.model';
export interface IOrderedItem {
  id?: number;
  quantity?: number;
  totalPrice?: number;
  status?: OrderedItemStatus;
  product?: IProduct | null;
  order?: IProductOrder;
}

export class OrderedItem implements IOrderedItem {
  constructor(
    public id?: number,
    public quantity?: number,
    public totalPrice?: number,
    public status?: OrderedItemStatus,
    public product?: IProduct | null,
    public order?: IProductOrder
  ) {}
}
