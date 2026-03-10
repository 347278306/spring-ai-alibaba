package com.hao.saa07.records;

/***
 * jdk14 引入 记录类record = entity + lombok
 * @param id
 * @param name
 * @param major
 * @param email
 */
public record StrudentRecord(String id, String name, String major, String email) {
}
