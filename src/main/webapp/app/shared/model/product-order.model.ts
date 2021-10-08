import { IInvoice } from '@/shared/model/invoice.model';
import { IOrderedItem } from '@/shared/model/ordered-item.model';
import { ICustomer } from '@/shared/model/customer.model';

import { OrderStatus } from '@/shared/model/enumerations/order-status.model';
export interface IProductOrder {
  id?: number;
  placedDate?: Date;
  status?: OrderStatus;
  code?: string;
  invoices?: IInvoice[] | null;
  orderedItems?: IOrderedItem[] | null;
  customer?: ICustomer;
}

export class ProductOrder implements IProductOrder {
  constructor(
    public id?: number,
    public placedDate?: Date,
    public status?: OrderStatus,
    public code?: string,
    public invoices?: IInvoice[] | null,
    public orderedItems?: IOrderedItem[] | null,
    public customer?: ICustomer
  ) {}
}
