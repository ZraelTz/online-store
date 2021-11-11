<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="storeApp.productRating.home.createOrEditLabel"
          data-cy="ProductRatingCreateUpdateHeading"
          v-text="$t('storeApp.productRating.home.createOrEditLabel')"
        >
          Create or edit a ProductRating
        </h2>
        <div>
          <div class="form-group" v-if="productRating.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="productRating.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productRating.rating')" for="product-rating-rating">Rating</label>
            <input
              type="number"
              class="form-control"
              name="rating"
              id="product-rating-rating"
              data-cy="rating"
              :class="{ valid: !$v.productRating.rating.$invalid, invalid: $v.productRating.rating.$invalid }"
              v-model.number="$v.productRating.rating.$model"
              required
            />
            <div v-if="$v.productRating.rating.$anyDirty && $v.productRating.rating.$invalid">
              <small class="form-text text-danger" v-if="!$v.productRating.rating.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.productRating.rating.min" v-text="$t('entity.validation.min', { min: 0 })">
                This field should be at least 0.
              </small>
              <small class="form-text text-danger" v-if="!$v.productRating.rating.max" v-text="$t('entity.validation.max', { max: 5 })">
                This field cannot be longer than 5 characters.
              </small>
              <small class="form-text text-danger" v-if="!$v.productRating.rating.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productRating.date')" for="product-rating-date">Date</label>
            <div class="d-flex">
              <input
                id="product-rating-date"
                data-cy="date"
                type="datetime-local"
                class="form-control"
                name="date"
                :class="{ valid: !$v.productRating.date.$invalid, invalid: $v.productRating.date.$invalid }"
                required
                :value="convertDateTimeFromServer($v.productRating.date.$model)"
                @change="updateInstantField('date', $event)"
              />
            </div>
            <div v-if="$v.productRating.date.$anyDirty && $v.productRating.date.$invalid">
              <small class="form-text text-danger" v-if="!$v.productRating.date.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.productRating.date.ZonedDateTimelocal"
                v-text="$t('entity.validation.ZonedDateTimelocal')"
              >
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productRating.user')" for="product-rating-user">User</label>
            <select class="form-control" id="product-rating-user" data-cy="user" name="user" v-model="productRating.user" required>
              <option v-if="!productRating.user" v-bind:value="null" selected></option>
              <option
                v-bind:value="productRating.user && userOption.id === productRating.user.id ? productRating.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div v-if="$v.productRating.user.$anyDirty && $v.productRating.user.$invalid">
            <small class="form-text text-danger" v-if="!$v.productRating.user.required" v-text="$t('entity.validation.required')">
              This field is required.
            </small>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productRating.product')" for="product-rating-product">Product</label>
            <select
              class="form-control"
              id="product-rating-product"
              data-cy="product"
              name="product"
              v-model="productRating.product"
              required
            >
              <option v-if="!productRating.product" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  productRating.product && productOption.id === productRating.product.id ? productRating.product : productOption
                "
                v-for="productOption in products"
                :key="productOption.id"
              >
                {{ productOption.name }}
              </option>
            </select>
          </div>
          <div v-if="$v.productRating.product.$anyDirty && $v.productRating.product.$invalid">
            <small class="form-text text-danger" v-if="!$v.productRating.product.required" v-text="$t('entity.validation.required')">
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
            :disabled="$v.productRating.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./product-rating-update.component.ts"></script>
