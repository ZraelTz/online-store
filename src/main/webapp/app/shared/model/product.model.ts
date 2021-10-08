import { IProductCategory } from '@/shared/model/product-category.model';

import { ProductSize } from '@/shared/model/enumerations/product-size.model';
export interface IProduct {
  id?: number;
  name?: string;
  material?: string | null;
  description?: string | null;
  price?: number;
  productSize?: ProductSize;
  imageContentType?: string | null;
  image?: string | null;
  productCategory?: IProductCategory | null;
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public name?: string,
    public material?: string | null,
    public description?: string | null,
    public price?: number,
    public productSize?: ProductSize,
    public imageContentType?: string | null,
    public image?: string | null,
    public productCategory?: IProductCategory | null
  ) {}
}
