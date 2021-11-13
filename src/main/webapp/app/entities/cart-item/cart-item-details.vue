<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="cartItem">
        <h2 class="jh-entity-heading" data-cy="cartItemDetailsHeading">
          <span v-text="$t('storeApp.cartItem.detail.title')">CartItem</span> {{ cartItem.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('storeApp.cartItem.quantity')">Quantity</span>
          </dt>
          <dd>
            <span>{{ cartItem.quantity }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.cartItem.cart')">Cart</span>
          </dt>
          <dd>
            <div v-if="cartItem.cart">
              <router-link :to="{ name: 'CartView', params: { cartId: cartItem.cart.id } }">{{ cartItem.cart.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="$t('storeApp.cartItem.product')">Product</span>
          </dt>
          <dd>
            <div v-if="cartItem.product">
              <router-link :to="{ name: 'ProductView', params: { productId: cartItem.product.id } }">{{
                cartItem.product.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="cartItem.id" :to="{ name: 'CartItemEdit', params: { cartItemId: cartItem.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./cart-item-details.component.ts"></script>
