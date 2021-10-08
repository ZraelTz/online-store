import { IInvoice } from '@/shared/model/invoice.model';

export interface IShipment {
  id?: number;
  trackingCode?: string | null;
  date?: Date;
  details?: string | null;
  invoice?: IInvoice;
}

export class Shipment implements IShipment {
  constructor(
    public id?: number,
    public trackingCode?: string | null,
    public date?: Date,
    public details?: string | null,
    public invoice?: IInvoice
  ) {}
}
