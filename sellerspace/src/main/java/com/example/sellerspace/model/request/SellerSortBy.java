package com.example.sellerspace.model.request;

import org.springframework.data.domain.Sort;

public enum SellerSortBy {
    SELLER_INFO_EXTERNAL_ID_ASC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.ASC, "externalId");
        }
    },
    SELLER_INFO_EXTERNAL_ID_DESC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.DESC, "externalId");
        }
    },
    NAME_ASC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.ASC, "name");
        }
    },
    NAME_DESC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.DESC, "name");
        }
    },
    MARKETPLACE_ID_ASC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.ASC, "marketplace.id");
        }
    },
    MARKETPLACE_ID_DESC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.DESC, "marketplace.id");
        }
    };

    public abstract Sort getSort();
}