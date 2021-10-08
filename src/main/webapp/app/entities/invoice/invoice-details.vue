<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="invoice">
        <h2 class="jh-entity-heading" data-cy="invoiceDetailsHeading">
          <span v-text="$t('storeApp.invoice.detail.title')">Invoice</span> {{ invoice.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('storeApp.invoice.date')">Date</span>
          </dt>
          <dd>
            <span v-if="invoice.date">{{ $d(Date.parse(invoice.date), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.invoice.details')">Details</span>
          </dt>
          <dd>
            <span>{{ invoice.details }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.invoice.status')">Status</span>
          </dt>
          <dd>
            <span v-text="$t('storeApp.InvoiceStatus.' + invoice.status)">{{ invoice.status }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.invoice.paymentMethod')">Payment Method</span>
          </dt>
          <dd>
            <span v-text="$t('storeApp.PaymentMethod.' + invoice.paymentMethod)">{{ invoice.paymentMethod }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.invoice.paymentDate')">Payment Date</span>
          </dt>
          <dd>
            <span v-if="invoice.paymentDate">{{ $d(Date.parse(invoice.paymentDate), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.invoice.paymentAmount')">Payment Amount</span>
          </dt>
          <dd>
            <span>{{ invoice.paymentAmount }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.invoice.code')">Code</span>
          </dt>
          <dd>
            <span>{{ invoice.code }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.invoice.order')">Order</span>
          </dt>
          <dd>
            <div v-if="invoice.order">
              <router-link :to="{ name: 'ProductOrderView', params: { productOrderId: invoice.order.id } }">{{
                invoice.order.code
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="invoice.id" :to="{ name: 'InvoiceEdit', params: { invoiceId: invoice.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary" v-if="hasAnyAuthority('ROLE_ADMIN')">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./invoice-details.component.ts"></script>
