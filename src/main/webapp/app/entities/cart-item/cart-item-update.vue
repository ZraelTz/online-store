<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="storeApp.cartItem.home.createOrEditLabel"
          data-cy="CartItemCreateUpdateHeading"
          v-text="$t('storeApp.cartItem.home.createOrEditLabel')"
        >
          Create or edit a CartItem
        </h2>
        <div>
          <div class="form-group" v-if="cartItem.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="cartItem.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.cartItem.quantity')" for="cart-item-quantity">Quantity</label>
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="cart-item-quantity"
              data-cy="quantity"
              :class="{ valid: !$v.cartItem.quantity.$invalid, invalid: $v.cartItem.quantity.$invalid }"
              v-model.number="$v.cartItem.quantity.$model"
              required
            />
            <div v-if="$v.cartItem.quantity.$anyDirty && $v.cartItem.quantity.$invalid">
              <small class="form-text text-danger" v-if="!$v.cartItem.quantity.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.cartItem.quantity.min" v-text="$t('entity.validation.min', { min: 0 })">
                This field should be at least 0.
              </small>
              <small class="form-text text-danger" v-if="!$v.cartItem.quantity.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.cartItem.product')" for="cart-item-product">Product</label>
            <select class="form-control" id="cart-item-product" data-cy="product" name="product" v-model="cartItem.product" required>
              <option v-if="!cartItem.product" v-bind:value="null" selected></option>
              <option
                v-bind:value="cartItem.product && productOption.id === cartItem.product.id ? cartItem.product : productOption"
                v-for="productOption in products"
                :key="productOption.id"
              >
                {{ productOption.name }}
              </option>
            </select>
          </div>
          <div v-if="$v.cartItem.product.$anyDirty && $v.cartItem.product.$invalid">
            <small class="form-text text-danger" v-if="!$v.cartItem.product.required" v-text="$t('entity.validation.required')">
              This field is required.
            </small>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.cartItem.customer')" for="cart-item-customer">Customer</label>
            <select class="form-control" id="cart-item-customer" data-cy="customer" name="customer" v-model="cartItem.customer" required>
              <option v-if="!cartItem.customer" v-bind:value="null" selected></option>
              <option
                v-bind:value="cartItem.customer && customerOption.id === cartItem.customer.id ? cartItem.customer : customerOption"
                v-for="customerOption in customers"
                :key="customerOption.id"
              >
                {{ customerOption.email }}
              </option>
            </select>
          </div>
          <div v-if="$v.cartItem.customer.$anyDirty && $v.cartItem.customer.$invalid">
            <small class="form-text text-danger" v-if="!$v.cartItem.customer.required" v-text="$t('entity.validation.required')">
              This field is required.
            </small>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.cartItem.cart')" for="cart-item-cart">Cart</label>
            <select class="form-control" id="cart-item-cart" data-cy="cart" name="cart" v-model="cartItem.cart">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="cartItem.cart && cartOption.id === cartItem.cart.id ? cartItem.cart : cartOption"
                v-for="cartOption in carts"
                :key="cartOption.id"
              >
                {{ cartOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.cartItem.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./cart-item-update.component.ts"></script>
