<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="storeApp.invoice.home.createOrEditLabel"
          data-cy="InvoiceCreateUpdateHeading"
          v-text="$t('storeApp.invoice.home.createOrEditLabel')"
        >
          Create or edit a Invoice
        </h2>
        <div>
          <div class="form-group" v-if="invoice.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="invoice.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.invoice.date')" for="invoice-date">Date</label>
            <div class="d-flex">
              <input
                id="invoice-date"
                data-cy="date"
                type="datetime-local"
                class="form-control"
                name="date"
                :class="{ valid: !$v.invoice.date.$invalid, invalid: $v.invoice.date.$invalid }"
                required
                :value="convertDateTimeFromServer($v.invoice.date.$model)"
                @change="updateInstantField('date', $event)"
              />
            </div>
            <div v-if="$v.invoice.date.$anyDirty && $v.invoice.date.$invalid">
              <small class="form-text text-danger" v-if="!$v.invoice.date.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.invoice.date.ZonedDateTimelocal"
                v-text="$t('entity.validation.ZonedDateTimelocal')"
              >
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.invoice.details')" for="invoice-details">Details</label>
            <input
              type="text"
              class="form-control"
              name="details"
              id="invoice-details"
              data-cy="details"
              :class="{ valid: !$v.invoice.details.$invalid, invalid: $v.invoice.details.$invalid }"
              v-model="$v.invoice.details.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.invoice.status')" for="invoice-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !$v.invoice.status.$invalid, invalid: $v.invoice.status.$invalid }"
              v-model="$v.invoice.status.$model"
              id="invoice-status"
              data-cy="status"
              required
            >
              <option value="PAID" v-bind:label="$t('storeApp.InvoiceStatus.PAID')">PAID</option>
              <option value="ISSUED" v-bind:label="$t('storeApp.InvoiceStatus.ISSUED')">ISSUED</option>
              <option value="CANCELLED" v-bind:label="$t('storeApp.InvoiceStatus.CANCELLED')">CANCELLED</option>
            </select>
            <div v-if="$v.invoice.status.$anyDirty && $v.invoice.status.$invalid">
              <small class="form-text text-danger" v-if="!$v.invoice.status.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.invoice.paymentMethod')" for="invoice-paymentMethod"
              >Payment Method</label
            >
            <select
              class="form-control"
              name="paymentMethod"
              :class="{ valid: !$v.invoice.paymentMethod.$invalid, invalid: $v.invoice.paymentMethod.$invalid }"
              v-model="$v.invoice.paymentMethod.$model"
              id="invoice-paymentMethod"
              data-cy="paymentMethod"
              required
            >
              <option value="CREDIT_CARD" v-bind:label="$t('storeApp.PaymentMethod.CREDIT_CARD')">CREDIT_CARD</option>
              <option value="CASH_ON_DELIVERY" v-bind:label="$t('storeApp.PaymentMethod.CASH_ON_DELIVERY')">CASH_ON_DELIVERY</option>
              <option value="BANK_TRANSFER" v-bind:label="$t('storeApp.PaymentMethod.BANK_TRANSFER')">BANK_TRANSFER</option>
              <option value="USSD" v-bind:label="$t('storeApp.PaymentMethod.USSD')">USSD</option>
              <option value="OFFLINE_PAYMENT" v-bind:label="$t('storeApp.PaymentMethod.OFFLINE_PAYMENT')">OFFLINE_PAYMENT</option>
            </select>
            <div v-if="$v.invoice.paymentMethod.$anyDirty && $v.invoice.paymentMethod.$invalid">
              <small class="form-text text-danger" v-if="!$v.invoice.paymentMethod.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.invoice.paymentDate')" for="invoice-paymentDate">Payment Date</label>
            <div class="d-flex">
              <input
                id="invoice-paymentDate"
                data-cy="paymentDate"
                type="datetime-local"
                class="form-control"
                name="paymentDate"
                :class="{ valid: !$v.invoice.paymentDate.$invalid, invalid: $v.invoice.paymentDate.$invalid }"
                required
                :value="convertDateTimeFromServer($v.invoice.paymentDate.$model)"
                @change="updateInstantField('paymentDate', $event)"
              />
            </div>
            <div v-if="$v.invoice.paymentDate.$anyDirty && $v.invoice.paymentDate.$invalid">
              <small class="form-text text-danger" v-if="!$v.invoice.paymentDate.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.invoice.paymentDate.ZonedDateTimelocal"
                v-text="$t('entity.validation.ZonedDateTimelocal')"
              >
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.invoice.paymentAmount')" for="invoice-paymentAmount"
              >Payment Amount</label
            >
            <input
              type="number"
              class="form-control"
              name="paymentAmount"
              id="invoice-paymentAmount"
              data-cy="paymentAmount"
              :class="{ valid: !$v.invoice.paymentAmount.$invalid, invalid: $v.invoice.paymentAmount.$invalid }"
              v-model.number="$v.invoice.paymentAmount.$model"
              required
            />
            <div v-if="$v.invoice.paymentAmount.$anyDirty && $v.invoice.paymentAmount.$invalid">
              <small class="form-text text-danger" v-if="!$v.invoice.paymentAmount.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.invoice.paymentAmount.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.invoice.code')" for="invoice-code">Code</label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="invoice-code"
              data-cy="code"
              :class="{ valid: !$v.invoice.code.$invalid, invalid: $v.invoice.code.$invalid }"
              v-model="$v.invoice.code.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.invoice.productOrder')" for="invoice-productOrder">Order</label>
            <select class="form-control" id="invoice-productOrder" data-cy="productOrder" name="productOrder" v-model="invoice.productOrder" required>
              <option v-if="!invoice.productOrder" v-bind:value="null" selected></option>
              <option
                v-bind:value="invoice.productOrder && productOrderOption.id === invoice.productOrder.id ? invoice.productOrder : productOrderOption"
                v-for="productOrderOption in productOrders"
                :key="productOrderOption.id"
              >
                {{ productOrderOption.code }}
              </option>
            </select>
          </div>
          <div v-if="$v.invoice.productOrder.$anyDirty && $v.invoice.productOrder.$invalid">
            <small class="form-text text-danger" v-if="!$v.invoice.productOrder.required" v-text="$t('entity.validation.required')">
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
            :disabled="$v.invoice.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./invoice-update.component.ts"></script>
