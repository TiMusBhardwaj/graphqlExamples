package com.example.sellerspace.model.request;

import com.example.sellerspace.entity.SellerInfoEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;

public enum SellerSortBy {
    SELLER_INFO_EXTERNAL_ID_ASC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.ASC, "externalId");
        }

        @Override
        public Order getCriteriaOrder(CriteriaBuilder cb, Root<?> root) {
            return cb.asc(root.get("externalId"));
        }
    },
    SELLER_INFO_EXTERNAL_ID_DESC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.DESC, "externalId");
        }
        @Override
        public Order getCriteriaOrder(CriteriaBuilder cb, Root<?> root) {
            return cb.desc(root.get("externalId"));
        }
    },
    NAME_ASC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.ASC, "name");
        }
        @Override
        public Order getCriteriaOrder(CriteriaBuilder cb, Root<?> root) {
            return cb.asc(root.get("name"));
        }
    },
    NAME_DESC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.DESC, "name");
        }
        @Override
        public Order getCriteriaOrder(CriteriaBuilder cb, Root<?> root) {
            return cb.desc(root.get("name"));
        }
    },
    MARKETPLACE_ID_ASC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.ASC, "marketplace.id");
        }
        @Override
        public Order getCriteriaOrder(CriteriaBuilder cb, Root<?> root) {
            return cb.asc(root.get("marketplace").get("id"));
        }
    },
    MARKETPLACE_ID_DESC {
        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.DESC, "marketplace.id");
        }
        @Override
        public Order getCriteriaOrder(CriteriaBuilder cb, Root<?> root) {
            return cb.desc(root.get("marketplace").get("id"));
        }
    };

    public abstract Sort getSort();
    public abstract Order getCriteriaOrder(CriteriaBuilder cb, Root<?> root);
}