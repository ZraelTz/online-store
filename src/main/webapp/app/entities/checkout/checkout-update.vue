<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="storeApp.checkout.home.createOrEditLabel"
          data-cy="CheckoutCreateUpdateHeading"
          v-text="$t('storeApp.checkout.home.createOrEditLabel')"
        >
          Create or edit a Checkout
        </h2>
        <div>
          <div class="form-group" v-if="checkout.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="checkout.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.checkout.quantity')" for="checkout-quantity">Quantity</label>
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="checkout-quantity"
              data-cy="quantity"
              :class="{ valid: !$v.checkout.quantity.$invalid, invalid: $v.checkout.quantity.$invalid }"
              v-model.number="$v.checkout.quantity.$model"
              required
            />
            <div v-if="$v.checkout.quantity.$anyDirty && $v.checkout.quantity.$invalid">
              <small class="form-text text-danger" v-if="!$v.checkout.quantity.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.checkout.quantity.min" v-text="$t('entity.validation.min', { min: 0 })">
                This field should be at least 0.
              </small>
              <small class="form-text text-danger" v-if="!$v.checkout.quantity.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.checkout.totalPrice')" for="checkout-totalPrice">Total Price</label>
            <input
              type="number"
              class="form-control"
              name="totalPrice"
              id="checkout-totalPrice"
              data-cy="totalPrice"
              :class="{ valid: !$v.checkout.totalPrice.$invalid, invalid: $v.checkout.totalPrice.$invalid }"
              v-model.number="$v.checkout.totalPrice.$model"
              required
            />
            <div v-if="$v.checkout.totalPrice.$anyDirty && $v.checkout.totalPrice.$invalid">
              <small class="form-text text-danger" v-if="!$v.checkout.totalPrice.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.checkout.totalPrice.min" v-text="$t('entity.validation.min', { min: 0 })">
                This field should be at least 0.
              </small>
              <small class="form-text text-danger" v-if="!$v.checkout.totalPrice.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div v-if="$v.checkout.cart.$anyDirty && $v.checkout.cart.$invalid">
            <small class="form-text text-danger" v-if="!$v.checkout.cart.required" v-text="$t('entity.validation.required')">
              This field is required.
            </small>
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
            :disabled="$v.checkout.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./checkout-update.component.ts"></script>
