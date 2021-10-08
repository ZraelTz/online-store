<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="storeApp.orderedItem.home.createOrEditLabel"
          data-cy="OrderedItemCreateUpdateHeading"
          v-text="$t('storeApp.orderedItem.home.createOrEditLabel')"
        >
          Create or edit a OrderedItem
        </h2>
        <div>
          <div class="form-group" v-if="orderedItem.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="orderedItem.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.orderedItem.quantity')" for="ordered-item-quantity">Quantity</label>
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="ordered-item-quantity"
              data-cy="quantity"
              :class="{ valid: !$v.orderedItem.quantity.$invalid, invalid: $v.orderedItem.quantity.$invalid }"
              v-model.number="$v.orderedItem.quantity.$model"
              required
            />
            <div v-if="$v.orderedItem.quantity.$anyDirty && $v.orderedItem.quantity.$invalid">
              <small class="form-text text-danger" v-if="!$v.orderedItem.quantity.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.orderedItem.quantity.min" v-text="$t('entity.validation.min', { min: 0 })">
                This field should be at least 0.
              </small>
              <small class="form-text text-danger" v-if="!$v.orderedItem.quantity.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.orderedItem.totalPrice')" for="ordered-item-totalPrice"
              >Total Price</label
            >
            <input
              type="number"
              class="form-control"
              name="totalPrice"
              id="ordered-item-totalPrice"
              data-cy="totalPrice"
              :class="{ valid: !$v.orderedItem.totalPrice.$invalid, invalid: $v.orderedItem.totalPrice.$invalid }"
              v-model.number="$v.orderedItem.totalPrice.$model"
              required
            />
            <div v-if="$v.orderedItem.totalPrice.$anyDirty && $v.orderedItem.totalPrice.$invalid">
              <small class="form-text text-danger" v-if="!$v.orderedItem.totalPrice.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.orderedItem.totalPrice.min" v-text="$t('entity.validation.min', { min: 0 })">
                This field should be at least 0.
              </small>
              <small class="form-text text-danger" v-if="!$v.orderedItem.totalPrice.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.orderedItem.status')" for="ordered-item-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !$v.orderedItem.status.$invalid, invalid: $v.orderedItem.status.$invalid }"
              v-model="$v.orderedItem.status.$model"
              id="ordered-item-status"
              data-cy="status"
              required
            >
              <option value="AVAILABLE" v-bind:label="$t('storeApp.OrderedItemStatus.AVAILABLE')">AVAILABLE</option>
              <option value="OUT_OF_STOCK" v-bind:label="$t('storeApp.OrderedItemStatus.OUT_OF_STOCK')">OUT_OF_STOCK</option>
              <option value="BACK_ORDER" v-bind:label="$t('storeApp.OrderedItemStatus.BACK_ORDER')">BACK_ORDER</option>
            </select>
            <div v-if="$v.orderedItem.status.$anyDirty && $v.orderedItem.status.$invalid">
              <small class="form-text text-danger" v-if="!$v.orderedItem.status.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.orderedItem.product')" for="ordered-item-product">Product</label>
            <select class="form-control" id="ordered-item-product" data-cy="product" name="product" v-model="orderedItem.product">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="orderedItem.product && productOption.id === orderedItem.product.id ? orderedItem.product : productOption"
                v-for="productOption in products"
                :key="productOption.id"
              >
                {{ productOption.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.orderedItem.order')" for="ordered-item-order">Order</label>
            <select class="form-control" id="ordered-item-order" data-cy="order" name="order" v-model="orderedItem.order" required>
              <option v-if="!orderedItem.order" v-bind:value="null" selected></option>
              <option
                v-bind:value="orderedItem.order && productOrderOption.id === orderedItem.order.id ? orderedItem.order : productOrderOption"
                v-for="productOrderOption in productOrders"
                :key="productOrderOption.id"
              >
                {{ productOrderOption.code }}
              </option>
            </select>
          </div>
          <div v-if="$v.orderedItem.order.$anyDirty && $v.orderedItem.order.$invalid">
            <small class="form-text text-danger" v-if="!$v.orderedItem.order.required" v-text="$t('entity.validation.required')">
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
            :disabled="$v.orderedItem.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./ordered-item-update.component.ts"></script>
