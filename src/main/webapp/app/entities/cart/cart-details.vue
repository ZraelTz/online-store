<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="cart">
        <h2 class="jh-entity-heading" data-cy="cartDetailsHeading">
          <span v-text="$t('storeApp.cart.detail.title')">Cart</span> {{ cart.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('storeApp.cart.date')">Date</span>
          </dt>
          <dd>
            <span v-if="cart.date">{{ $d(Date.parse(cart.date), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.cart.customer')">Customer</span>
          </dt>
          <dd>
            <div v-if="cart.customer">
              <router-link :to="{ name: 'CustomerView', params: { customerId: cart.customer.id } }">{{ cart.customer.email }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="$t('storeApp.cart.checkout')">Checkout</span>
          </dt>
          <dd>
            <div v-if="cart.checkout">
              <router-link :to="{ name: 'CheckoutView', params: { checkoutId: cart.checkout.id } }">{{
                cart.checkout.totalPrice
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="cart.id" :to="{ name: 'CartEdit', params: { cartId: cart.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./cart-details.component.ts"></script>
