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
            <label class="form-control-label" v-text="$t('storeApp.productRating.value')" for="product-rating-value">Value</label>
            <input
              type="number"
              class="form-control"
              name="value"
              id="product-rating-value"
              data-cy="value"
              :class="{ valid: !$v.productRating.value.$invalid, invalid: $v.productRating.value.$invalid }"
              v-model.number="$v.productRating.value.$model"
              required
            />
            <div v-if="$v.productRating.value.$anyDirty && $v.productRating.value.$invalid">
              <small class="form-text text-danger" v-if="!$v.productRating.value.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.productRating.value.min" v-text="$t('entity.validation.min', { min: 0 })">
                This field should be at least 0.
              </small>
              <small class="form-text text-danger" v-if="!$v.productRating.value.max" v-text="$t('entity.validation.max', { max: 5 })">
                This field cannot be longer than 5 characters.
              </small>
              <small class="form-text text-danger" v-if="!$v.productRating.value.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productRating.productId')" for="product-rating-productId"
              >Product Id</label
            >
            <input
              type="number"
              class="form-control"
              name="productId"
              id="product-rating-productId"
              data-cy="productId"
              :class="{ valid: !$v.productRating.productId.$invalid, invalid: $v.productRating.productId.$invalid }"
              v-model.number="$v.productRating.productId.$model"
              required
            />
            <div v-if="$v.productRating.productId.$anyDirty && $v.productRating.productId.$invalid">
              <small class="form-text text-danger" v-if="!$v.productRating.productId.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.productRating.productId.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productRating.userId')" for="product-rating-userId">User Id</label>
            <input
              type="number"
              class="form-control"
              name="userId"
              id="product-rating-userId"
              data-cy="userId"
              :class="{ valid: !$v.productRating.userId.$invalid, invalid: $v.productRating.userId.$invalid }"
              v-model.number="$v.productRating.userId.$model"
              required
            />
            <div v-if="$v.productRating.userId.$anyDirty && $v.productRating.userId.$invalid">
              <small class="form-text text-danger" v-if="!$v.productRating.userId.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.productRating.userId.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productRating.productRating')" for="product-rating-productRating"
              >Product Rating</label
            >
            <select
              class="form-control"
              id="product-rating-productRating"
              data-cy="productRating"
              name="productRating"
              v-model="productRating.productRating"
              required
            >
              <option v-if="!productRating.productRating" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  productRating.productRating && productOption.id === productRating.productRating.id
                    ? productRating.productRating
                    : productOption
                "
                v-for="productOption in products"
                :key="productOption.id"
              >
                {{ productOption.id }}
              </option>
            </select>
          </div>
          <div v-if="$v.productRating.productRating.$anyDirty && $v.productRating.productRating.$invalid">
            <small class="form-text text-danger" v-if="!$v.productRating.productRating.required" v-text="$t('entity.validation.required')">
              This field is required.
            </small>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.productRating.rating')" for="product-rating-rating">Rating</label>
            <select class="form-control" id="product-rating-rating" data-cy="rating" name="rating" v-model="productRating.rating" required>
              <option v-if="!productRating.rating" v-bind:value="null" selected></option>
              <option
                v-bind:value="productRating.rating && userOption.id === productRating.rating.id ? productRating.rating : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div v-if="$v.productRating.rating.$anyDirty && $v.productRating.rating.$invalid">
            <small class="form-text text-danger" v-if="!$v.productRating.rating.required" v-text="$t('entity.validation.required')">
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
