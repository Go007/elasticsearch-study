-- Create table
create table CS_MASTER_TGT.NEWS_LABEL_JX
(
  id                 NUMBER(22) not null,
  news_basicinfo_sid NUMBER(22) not null,
  company_id         NUMBER(16),
  label              VARCHAR2(60),
  sentimental        INTEGER,
  importance         INTEGER,
  is_del             INTEGER,
  create_dt          TIMESTAMP(6),
  updt_dt            TIMESTAMP(6)
)
tablespace CS_MASTER_TGT
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 80K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns
comment on column CS_MASTER_TGT.NEWS_LABEL_JX.id
  is '舆情机构映射标识符';
comment on column CS_MASTER_TGT.NEWS_LABEL_JX.news_basicinfo_sid
  is '舆情信息的ID';
comment on column CS_MASTER_TGT.NEWS_LABEL_JX.company_id
  is '企业标识符';
comment on column CS_MASTER_TGT.NEWS_LABEL_JX.label
  is '业务标签';
comment on column CS_MASTER_TGT.NEWS_LABEL_JX.sentimental
  is '标签情感值';
comment on column CS_MASTER_TGT.NEWS_LABEL_JX.importance
  is '重要性';
comment on column CS_MASTER_TGT.NEWS_LABEL_JX.is_del
  is '是否删除';
comment on column CS_MASTER_TGT.NEWS_LABEL_JX.create_dt
  is '入库时间';
comment on column CS_MASTER_TGT.NEWS_LABEL_JX.updt_dt
  is '修改时间';
-- Create/Recreate indexes
create index CS_MASTER_TGT.IDX_NEWS_LABEL001 on CS_MASTER_TGT.NEWS_LABEL_JX (ID, IS_DEL)
  tablespace CS_MASTER_TGT
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
create index CS_MASTER_TGT.IDX_NEWS_LABEL002 on CS_MASTER_TGT.NEWS_LABEL_JX (NEWS_BASICINFO_SID, IS_DEL)
  tablespace CS_MASTER_TGT
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Create/Recreate primary, unique and foreign key constraints
alter table CS_MASTER_TGT.NEWS_LABEL_JX
  add constraint PK_NEWS_LABEL_JX primary key (ID)
  using index
  tablespace CS_MASTER_TGT
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Grant/Revoke object privileges
grant select on CS_MASTER_TGT.NEWS_LABEL_JX to KFSHOW with grant option;
