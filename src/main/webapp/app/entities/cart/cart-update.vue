<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="storeApp.cart.home.createOrEditLabel" data-cy="CartCreateUpdateHeading" v-text="$t('storeApp.cart.home.createOrEditLabel')">
          Create or edit a Cart
        </h2>
        <div>
          <div class="form-group" v-if="cart.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="cart.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.cart.date')" for="cart-date">Date</label>
            <div class="d-flex">
              <input
                id="cart-date"
                data-cy="date"
                type="datetime-local"
                class="form-control"
                name="date"
                :class="{ valid: !$v.cart.date.$invalid, invalid: $v.cart.date.$invalid }"
                required
                :value="convertDateTimeFromServer($v.cart.date.$model)"
                @change="updateInstantField('date', $event)"
              />
            </div>
            <div v-if="$v.cart.date.$anyDirty && $v.cart.date.$invalid">
              <small class="form-text text-danger" v-if="!$v.cart.date.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.cart.date.ZonedDateTimelocal"
                v-text="$t('entity.validation.ZonedDateTimelocal')"
              >
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.cart.customer')" for="cart-customer">Customer</label>
            <select class="form-control" id="cart-customer" data-cy="customer" name="customer" v-model="cart.customer" required>
              <option v-if="!cart.customer" v-bind:value="null" selected></option>
              <option
                v-bind:value="cart.customer && customerOption.id === cart.customer.id ? cart.customer : customerOption"
                v-for="customerOption in customers"
                :key="customerOption.id"
              >
                {{ customerOption.email }}
              </option>
            </select>
          </div>
          <div v-if="$v.cart.customer.$anyDirty && $v.cart.customer.$invalid">
            <small class="form-text text-danger" v-if="!$v.cart.customer.required" v-text="$t('entity.validation.required')">
              This field is required.
            </small>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.cart.checkout')" for="cart-checkout">Checkout</label>
            <select class="form-control" id="cart-checkout" data-cy="checkout" name="checkout" v-model="cart.checkout">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="cart.checkout && checkoutOption.id === cart.checkout.id ? cart.checkout : checkoutOption"
                v-for="checkoutOption in checkouts"
                :key="checkoutOption.id"
              >
                {{ checkoutOption.totalPrice }}
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
            :disabled="$v.cart.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./cart-update.component.ts"></script>
