<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="storeApp.productOrder.home.createOrEditLabel"
          data-cy="ProductOrderCreateUpdateHeading"
          v-text="$t('storeApp.productOrder.home.createOrEditLabel')"
        >
          Create or edit a ProductOrder
        </h2>
        <div>
          <div class="form-group" v-if="productOrder.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="productOrder.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productOrder.placedDate')" for="product-order-placedDate"
              >Placed Date</label
            >
            <div class="d-flex">
              <input
                id="product-order-placedDate"
                data-cy="placedDate"
                type="datetime-local"
                class="form-control"
                name="placedDate"
                :class="{ valid: !$v.productOrder.placedDate.$invalid, invalid: $v.productOrder.placedDate.$invalid }"
                required
                :value="convertDateTimeFromServer($v.productOrder.placedDate.$model)"
                @change="updateInstantField('placedDate', $event)"
              />
            </div>
            <div v-if="$v.productOrder.placedDate.$anyDirty && $v.productOrder.placedDate.$invalid">
              <small class="form-text text-danger" v-if="!$v.productOrder.placedDate.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.productOrder.placedDate.ZonedDateTimelocal"
                v-text="$t('entity.validation.ZonedDateTimelocal')"
              >
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productOrder.status')" for="product-order-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !$v.productOrder.status.$invalid, invalid: $v.productOrder.status.$invalid }"
              v-model="$v.productOrder.status.$model"
              id="product-order-status"
              data-cy="status"
              required
            >
              <option value="COMPLETED" v-bind:label="$t('storeApp.OrderStatus.COMPLETED')">COMPLETED</option>
              <option value="PENDING" v-bind:label="$t('storeApp.OrderStatus.PENDING')">PENDING</option>
              <option value="CANCELLED" v-bind:label="$t('storeApp.OrderStatus.CANCELLED')">CANCELLED</option>
            </select>
            <div v-if="$v.productOrder.status.$anyDirty && $v.productOrder.status.$invalid">
              <small class="form-text text-danger" v-if="!$v.productOrder.status.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productOrder.code')" for="product-order-code">Code</label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="product-order-code"
              data-cy="code"
              :class="{ valid: !$v.productOrder.code.$invalid, invalid: $v.productOrder.code.$invalid }"
              v-model="$v.productOrder.code.$model"
              required
            />
            <div v-if="$v.productOrder.code.$anyDirty && $v.productOrder.code.$invalid">
              <small class="form-text text-danger" v-if="!$v.productOrder.code.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productOrder.customer')" for="product-order-customer">Customer</label>
            <select
              class="form-control"
              id="product-order-customer"
              data-cy="customer"
              name="customer"
              v-model="productOrder.customer"
              required
            >
              <option v-if="!productOrder.customer" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  productOrder.customer && customerOption.id === productOrder.customer.id ? productOrder.customer : customerOption
                "
                v-for="customerOption in customers"
                :key="customerOption.id"
              >
                {{ customerOption.email }}
              </option>
            </select>
          </div>
          <div v-if="$v.productOrder.customer.$anyDirty && $v.productOrder.customer.$invalid">
            <small class="form-text text-danger" v-if="!$v.productOrder.customer.required" v-text="$t('entity.validation.required')">
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
            :disabled="$v.productOrder.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./product-order-update.component.ts"></script>
