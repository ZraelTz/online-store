import { IShipment } from '@/shared/model/shipment.model';
import { IProductOrder } from '@/shared/model/product-order.model';

import { InvoiceStatus } from '@/shared/model/enumerations/invoice-status.model';
import { PaymentMethod } from '@/shared/model/enumerations/payment-method.model';
export interface IInvoice {
  id?: number;
  date?: Date;
  details?: string | null;
  status?: InvoiceStatus;
  paymentMethod?: PaymentMethod;
  paymentDate?: Date;
  paymentAmount?: number;
  code?: string | null;
  shipments?: IShipment[] | null;
  order?: IProductOrder;
}

export class Invoice implements IInvoice {
  constructor(
    public id?: number,
    public date?: Date,
    public details?: string | null,
    public status?: InvoiceStatus,
    public paymentMethod?: PaymentMethod,
    public paymentDate?: Date,
    public paymentAmount?: number,
    public code?: string | null,
    public shipments?: IShipment[] | null,
    public order?: IProductOrder
  ) {}
}
