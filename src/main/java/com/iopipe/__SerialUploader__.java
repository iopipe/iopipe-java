package com.iopipe;

/**
 * This is an uploader which is completely serial based and it will only
 * upload events one at a time, blocking for each one. It cannot handle
 * multiple concurrent invocations at once.
 *
 * @since 2018/07/23
 */
final class __SerialUploader__
	implements IOpipeEventUploader
{
}

