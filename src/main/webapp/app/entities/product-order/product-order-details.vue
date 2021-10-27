<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="productOrder">
        <h2 class="jh-entity-heading" data-cy="productOrderDetailsHeading">
          <span v-text="$t('storeApp.productOrder.detail.title')">ProductOrder</span> {{ productOrder.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('storeApp.productOrder.placedDate')">Placed Date</span>
          </dt>
          <dd>
            <span v-if="productOrder.placedDate">{{ $d(Date.parse(productOrder.placedDate), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.productOrder.status')">Status</span>
          </dt>
          <dd>
            <span v-text="$t('storeApp.OrderStatus.' + productOrder.status)">{{ productOrder.status }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.productOrder.code')">Code</span>
          </dt>
          <dd>
            <span>{{ productOrder.code }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.productOrder.customer')">Customer</span>
          </dt>
          <dd>
            <div v-if="productOrder.customer">
              <router-link :to="{ name: 'CustomerView', params: { customerId: productOrder.customer.id } }">{{
                productOrder.customer.email
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="productOrder.id"
          :to="{ name: 'ProductOrderEdit', params: { productOrderId: productOrder.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./product-order-details.component.ts"></script>
