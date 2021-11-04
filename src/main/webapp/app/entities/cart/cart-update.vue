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
            <label class="form-control-label" v-text="$t('storeApp.cart.quantity')" for="cart-quantity">Quantity</label>
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="cart-quantity"
              data-cy="quantity"
              :class="{ valid: !$v.cart.quantity.$invalid, invalid: $v.cart.quantity.$invalid }"
              v-model.number="$v.cart.quantity.$model"
              required
            />
            <div v-if="$v.cart.quantity.$anyDirty && $v.cart.quantity.$invalid">
              <small class="form-text text-danger" v-if="!$v.cart.quantity.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.cart.quantity.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
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
            <label class="form-control-label" v-text="$t('storeApp.cart.user')" for="cart-user">User</label>
            <select class="form-control" id="cart-user" data-cy="user" name="user" v-model="cart.user" required>
              <option v-if="!cart.user" v-bind:value="null" selected></option>
              <option
                v-bind:value="cart.user && userOption.id === cart.user.id ? cart.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div v-if="$v.cart.user.$anyDirty && $v.cart.user.$invalid">
            <small class="form-text text-danger" v-if="!$v.cart.user.required" v-text="$t('entity.validation.required')">
              This field is required.
            </small>
          </div>
          <div v-if="$v.cart.item.$anyDirty && $v.cart.item.$invalid">
            <small class="form-text text-danger" v-if="!$v.cart.item.required" v-text="$t('entity.validation.required')">
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
