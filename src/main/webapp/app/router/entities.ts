import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore

// prettier-ignore
const Customer = () => import('@/entities/customer/customer.vue');
// prettier-ignore
const CustomerUpdate = () => import('@/entities/customer/customer-update.vue');
// prettier-ignore
const CustomerDetails = () => import('@/entities/customer/customer-details.vue');
// prettier-ignore
const Invoice = () => import('@/entities/invoice/invoice.vue');
// prettier-ignore
const InvoiceUpdate = () => import('@/entities/invoice/invoice-update.vue');
// prettier-ignore
const InvoiceDetails = () => import('@/entities/invoice/invoice-details.vue');
// prettier-ignore
const OrderedItem = () => import('@/entities/ordered-item/ordered-item.vue');
// prettier-ignore
const OrderedItemUpdate = () => import('@/entities/ordered-item/ordered-item-update.vue');
// prettier-ignore
const OrderedItemDetails = () => import('@/entities/ordered-item/ordered-item-details.vue');
// prettier-ignore
const Product = () => import('@/entities/product/product.vue');
// prettier-ignore
const ProductUpdate = () => import('@/entities/product/product-update.vue');
// prettier-ignore
const ProductDetails = () => import('@/entities/product/product-details.vue');
// prettier-ignore
const ProductCategory = () => import('@/entities/product-category/product-category.vue');
// prettier-ignore
const ProductCategoryUpdate = () => import('@/entities/product-category/product-category-update.vue');
// prettier-ignore
const ProductCategoryDetails = () => import('@/entities/product-category/product-category-details.vue');
// prettier-ignore
const ProductOrder = () => import('@/entities/product-order/product-order.vue');
// prettier-ignore
const ProductOrderUpdate = () => import('@/entities/product-order/product-order-update.vue');
// prettier-ignore
const ProductOrderDetails = () => import('@/entities/product-order/product-order-details.vue');
// prettier-ignore
const Shipment = () => import('@/entities/shipment/shipment.vue');
// prettier-ignore
const ShipmentUpdate = () => import('@/entities/shipment/shipment-update.vue');
// prettier-ignore
const ShipmentDetails = () => import('@/entities/shipment/shipment-details.vue');
// prettier-ignore
const Vendor = () => import('@/entities/vendor/vendor.vue');
// prettier-ignore
const VendorUpdate = () => import('@/entities/vendor/vendor-update.vue');
// prettier-ignore
const VendorDetails = () => import('@/entities/vendor/vendor-details.vue');
// prettier-ignore
const ProductRating = () => import('@/entities/product-rating/product-rating.vue');
// prettier-ignore
const ProductRatingUpdate = () => import('@/entities/product-rating/product-rating-update.vue');
// prettier-ignore
const ProductRatingDetails = () => import('@/entities/product-rating/product-rating-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/customer',
    name: 'Customer',
    component: Customer,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/customer/new',
    name: 'CustomerCreate',
    component: CustomerUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/customer/:customerId/edit',
    name: 'CustomerEdit',
    component: CustomerUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/customer/:customerId/view',
    name: 'CustomerView',
    component: CustomerDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/invoice',
    name: 'Invoice',
    component: Invoice,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/invoice/new',
    name: 'InvoiceCreate',
    component: InvoiceUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/invoice/:invoiceId/edit',
    name: 'InvoiceEdit',
    component: InvoiceUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/invoice/:invoiceId/view',
    name: 'InvoiceView',
    component: InvoiceDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/ordered-item',
    name: 'OrderedItem',
    component: OrderedItem,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/ordered-item/new',
    name: 'OrderedItemCreate',
    component: OrderedItemUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/ordered-item/:orderedItemId/edit',
    name: 'OrderedItemEdit',
    component: OrderedItemUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/ordered-item/:orderedItemId/view',
    name: 'OrderedItemView',
    component: OrderedItemDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product',
    name: 'Product',
    component: Product,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product/new',
    name: 'ProductCreate',
    component: ProductUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product/:productId/edit',
    name: 'ProductEdit',
    component: ProductUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product/:productId/view',
    name: 'ProductView',
    component: ProductDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-category',
    name: 'ProductCategory',
    component: ProductCategory,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-category/new',
    name: 'ProductCategoryCreate',
    component: ProductCategoryUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-category/:productCategoryId/edit',
    name: 'ProductCategoryEdit',
    component: ProductCategoryUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-category/:productCategoryId/view',
    name: 'ProductCategoryView',
    component: ProductCategoryDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-order',
    name: 'ProductOrder',
    component: ProductOrder,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-order/new',
    name: 'ProductOrderCreate',
    component: ProductOrderUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-order/:productOrderId/edit',
    name: 'ProductOrderEdit',
    component: ProductOrderUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-order/:productOrderId/view',
    name: 'ProductOrderView',
    component: ProductOrderDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/shipment',
    name: 'Shipment',
    component: Shipment,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/shipment/new',
    name: 'ShipmentCreate',
    component: ShipmentUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/shipment/:shipmentId/edit',
    name: 'ShipmentEdit',
    component: ShipmentUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/shipment/:shipmentId/view',
    name: 'ShipmentView',
    component: ShipmentDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/vendor',
    name: 'Vendor',
    component: Vendor,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/vendor/new',
    name: 'VendorCreate',
    component: VendorUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/vendor/:vendorId/edit',
    name: 'VendorEdit',
    component: VendorUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/vendor/:vendorId/view',
    name: 'VendorView',
    component: VendorDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-rating',
    name: 'ProductRating',
    component: ProductRating,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-rating/new',
    name: 'ProductRatingCreate',
    component: ProductRatingUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-rating/:productRatingId/edit',
    name: 'ProductRatingEdit',
    component: ProductRatingUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/product-rating/:productRatingId/view',
    name: 'ProductRatingView',
    component: ProductRatingDetails,
    meta: { authorities: [Authority.USER] },
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
