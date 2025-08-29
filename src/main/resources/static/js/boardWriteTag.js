
function write_append() {
    const div_list = document.getElementsByClassName('input-images');
    let count = div_list.length+1;

    let wrap_main = document.createElement("div");
    wrap_main.setAttribute("class", "wrap_main");
    wrap_main.style.marginTop="35px";

    let col_lg_6 = document.createElement("div");
    col_lg_6.setAttribute("class", "col-lg-6");
    col_lg_6.style.float="left";

    let about_thumb = document.createElement("div")
    about_thumb.setAttribute("class", "about_thumb mb30-smd input-images");

    let img_fluid = document.createElement("img");
    img_fluid.setAttribute("src", "/images/about/1.jpg");
    img_fluid.setAttribute("alt", "1.jpg");
    img_fluid.setAttribute("id", "boardWrite"+count);
    img_fluid.style.height="500px";

    let popupiframe = document.createElement("a");
    popupiframe.setAttribute("class", "popupiframe popup-youtube");
    popupiframe.setAttribute("onclick", "$('#file').trigger('click')");
    popupiframe.setAttribute("href", "javascript:void(0);");

    let popupiframe_img = document.createElement("img");
    popupiframe_img.setAttribute("src", "/images/icons/upload.svg");
    popupiframe_img.setAttribute("alt", "play1.svg");
    popupiframe_img.style.maxWidth="66%";

    popupiframe.appendChild(popupiframe_img);

    let file = document.createElement("input");
    file.setAttribute("type", "file");
    file.setAttribute("multiple", "multiple");
    file.setAttribute("name", "file");
    file.setAttribute("class", "file_01");
    file.setAttribute("id", "file");
    file.setAttribute("onChange", "readURL(e);");
    file.style.display="none";

    about_thumb.appendChild(img_fluid);
    about_thumb.appendChild(popupiframe);
    about_thumb.appendChild(file);

    col_lg_6.appendChild(about_thumb);
    wrap_main.appendChild(col_lg_6);

    let col_lg_6_2 = document.createElement("div");
    col_lg_6_2.setAttribute("class", "col-lg-6");
    col_lg_6_2.style.float="left";

    let col_8 = document.createElement("div");
    col_8.setAttribute("class", "col-8");
    col_8.style.float="left";

    let title_label = document.createElement("label");
    title_label.innerHTML="제목";

    let form_group = document.createElement("div");
    form_group.setAttribute("class", "form-group input-group main_004");

    let title = document.createElement("input");
    title.setAttribute("type", "text");
    title.setAttribute("class", "form-control");
    title.setAttribute("id", "title");
    title.setAttribute("name", "title");
    title.setAttribute("placeholder", "제목을 입력해주세요");

    form_group.appendChild(title);
    col_8.appendChild(title_label);
    col_8.appendChild(form_group);
    col_lg_6_2.appendChild(col_8);

    let col_4 = document.createElement("div");
    col_4.setAttribute("class", "col-4");
    col_4.style.float="left";
    col_4.style.marginBottom="21px";

    let grade_label = document.createElement("label");
    grade_label.innerHTML="평점";

    let star_rating = document.createElement("div");
    star_rating.setAttribute("class", "star-rating space-x-4 mx-auto");

    let star_5 = document.createElement("input");
    star_5.setAttribute("type", "radio");
    star_5.setAttribute("id", "5-stars"+count);
    star_5.setAttribute("name", "rating"+count);
    star_5.setAttribute("value", "5");
    star_5.setAttribute("v-model", "ratings");

    let star_5_label = document.createElement("label");
    star_5_label.setAttribute("for", "5-stars"+count);
    star_5_label.setAttribute("class", "star");
    star_5_label.innerHTML="★";

    let star_4 = document.createElement("input");
    star_4.setAttribute("type", "radio");
    star_4.setAttribute("id", "4-stars"+count);
    star_4.setAttribute("name", "rating"+count);
    star_4.setAttribute("value", "4");
    star_4.setAttribute("v-model", "ratings");

    let star_4_label = document.createElement("label");
    star_4_label.setAttribute("for", "4-stars"+count);
    star_4_label.setAttribute("class", "star");
    star_4_label.innerHTML="★";

    let star_3 = document.createElement("input");
    star_3.setAttribute("type", "radio");
    star_3.setAttribute("id", "3-stars"+count);
    star_3.setAttribute("name", "rating"+count);
    star_3.setAttribute("value", "3");
    star_3.setAttribute("v-model", "ratings");

    let star_3_label = document.createElement("label");
    star_3_label.setAttribute("for", "3-stars"+count);
    star_3_label.setAttribute("class", "star");
    star_3_label.innerHTML="★";

    let star_2 = document.createElement("input");
    star_2.setAttribute("type", "radio");
    star_2.setAttribute("id", "2-stars"+count);
    star_2.setAttribute("name", "rating"+count);
    star_2.setAttribute("value", "2");
    star_2.setAttribute("v-model", "ratings");

    let star_2_label = document.createElement("label");
    star_2_label.setAttribute("for", "2-stars"+count);
    star_2_label.setAttribute("class", "star");
    star_2_label.innerHTML="★";

    let star_1 = document.createElement("input");
    star_1.setAttribute("type", "radio");
    star_1.setAttribute("id", "1-stars"+count);
    star_1.setAttribute("name", "rating"+count);
    star_1.setAttribute("value", "1");
    star_1.setAttribute("v-model", "ratings");

    let star_1_label = document.createElement("label");
    star_1_label.setAttribute("for", "1-stars"+count);
    star_1_label.setAttribute("class", "star");
    star_1_label.innerHTML="★";

    star_rating.appendChild(star_5);
    star_rating.appendChild(star_5_label);
    star_rating.appendChild(star_4);
    star_rating.appendChild(star_4_label);
    star_rating.appendChild(star_3);
    star_rating.appendChild(star_3_label);
    star_rating.appendChild(star_2);
    star_rating.appendChild(star_2_label);
    star_rating.appendChild(star_1);
    star_rating.appendChild(star_1_label);

    col_4.appendChild(grade_label);
    col_4.appendChild(star_rating);
    col_lg_6_2.appendChild(col_4);

    let col_8_2 = document.createElement("div");
    col_8_2.setAttribute("class", "col-8");
    col_8_2.style.float="left";

    let place_label = document.createElement("label");
    place_label.innerHTML="장소명";
    let form_group_2 = document.createElement("div");
    form_group_2.setAttribute("class", "form-group input-group main_004");
    let addr = document.createElement("input");
    addr.setAttribute("type", "text");
    addr.setAttribute("class", "form-control");
    addr.setAttribute("name", "addr");
    addr.setAttribute("placeholder", "장소을 입력해주세요");

    form_group_2.appendChild(addr);
    col_8_2.appendChild(place_label);
    col_8_2.appendChild(form_group_2);
    col_lg_6_2.appendChild(col_8_2);

    let col_4_2 = document.createElement("div");
    col_4_2.setAttribute("class", "col-4")
    col_4_2.style.float="left";

    let tag_label = document.createElement("label");
    tag_label.innerHTML="태그";
    let form_group_3 = document.createElement("div");
    form_group_3.setAttribute("class", "form-group input-group main_004");
    let hashTag = document.createElement("input");
    hashTag.setAttribute("type", "text");
    hashTag.setAttribute("class", "form-control");
    hashTag.setAttribute("id", "keyMain");
    hashTag.setAttribute("name", "hashTag");
    hashTag.setAttribute("placeholder", "#태그");

    form_group_3.appendChild(hashTag);
    col_4_2.appendChild(tag_label);
    col_4_2.appendChild(form_group_3);
    col_lg_6_2.appendChild(col_4_2);

    let board_content = document.createElement("textarea");
    board_content.setAttribute("class", "form-control");
    board_content.setAttribute("placeholder", "내용을 입력해주세요");
    board_content.setAttribute("name", "board_content");
    board_content.style.paddingLeft="15px";
    board_content.style.paddingRight="15px";
    board_content.style.marginLeft="15px";
    board_content.style.width="95%";
    board_content.style.height="298px";

    col_lg_6_2.appendChild(board_content);

    // let col_lg_12 = document.createElement("div");
    // col_lg_12.setAttribute("class", "col-lg-12");
    // let br = document.createElement("br");
    // let hr = document.createElement("hr");
    // hr.style.color="black";
    // col_lg_12.appendChild(br);
    // col_lg_12.appendChild(hr);
    wrap_main.appendChild(col_lg_6_2);

    $('#row').append(wrap_main);
    // let li_001 = document.createElement("li");
    // li_001.setAttribute("class", "li_001");
    //
    // let li_002 = document.createElement("li");
    // li_002.setAttribute("class", "li_001");
    //
    // let ol_002 = document.createElement("ol");
    // ol_002.setAttribute("class", "ol_002");
    //
    // let warp_001 = document.createElement("div");
    // warp_001.setAttribute("class", "warp_001");
    //
    // let postion_sapn = document.createElement("span");
    // let postion_title = document.createElement("label");
    // postion_title.innerHTML="장소명";
    //
    // let postion_addr = document.createElement("input");
    // postion_addr.setAttribute("type","text");
    // postion_addr.setAttribute("name","addr");
    // postion_addr.style.border="none";
    // postion_addr.style.display="inline";
    // postion_addr.style.marginLeft="10px";
    //
    // let warp_002 = document.createElement("div");
    // warp_002.setAttribute("class", "warp_002");
    //
    // let photo_001 = document.createElement("div");
    // photo_001.setAttribute("class", "photo_001");
    //
    // let photo_btn = document.createElement("button");
    // photo_btn.setAttribute("class", "photo_btn");
    // photo_btn.setAttribute("id","photo_btn"+count);
    // photo_btn.setAttribute("type","button");
    // photo_btn.setAttribute("onclick","photo_event();");
    //
    // let photo_002 = document.createElement("div");
    // photo_002.setAttribute("class", "photo_002");
    //
    // let img_tag = document.createElement("img");
    //
    // img_tag.setAttribute("src","");
    // img_tag.setAttribute("class", "input-image")
    // img_tag.setAttribute("id", "input-image"+count);
    // img_tag.style.width = "100%";
    // img_tag.style.height = "100%";
    // img_tag.style.objectFit = "cover";
    //
    // let write_001 = document.createElement("div");
    // write_001.setAttribute("class", "write_001");
    //
    // let write_002 = document.createElement("div");
    // write_002.setAttribute("class", "write_002");
    //
    // let title_001 = document.createElement("div");
    // title_001.setAttribute("class", "title_001");
    //
    //
    // let span_tag = document.createElement("span");
    //
    // let text_h4 = document.createElement("h4");
    // text_h4.innerHTML = "제목을 적어주세요:)";
    //
    // let text_tag = document.createElement("input");
    // text_tag.setAttribute("type", "text");
    // text_tag.setAttribute("class", "title_002");
    // text_tag.setAttribute("name","title");
    // text_tag.style.marginTop = "10px";
    //
    //
    // let select_span = document.createElement("span");
    //
    // let select_star = document.createElement("select");
    // select_star.setAttribute("class", "keyword_main");
    // select_star.setAttribute("name", "star");
    // select_star.style.boxSizing="content-box";
    // select_star.style.width="60px";
    //
    // let option_main = document.createElement("option");
    // option_main.setAttribute("value","");
    // option_main.setAttribute("selected", "selected");
    // option_main.innerHTML="평점";
    //
    // let option_1 = document.createElement("option");
    // option_1.setAttribute("value", "1");
    // option_1.innerHTML='1';
    //
    // let option_2 = document.createElement("option");
    // option_2.setAttribute("value", "2");
    // option_2.innerHTML='2';
    //
    // let option_3 = document.createElement("option");
    // option_3.setAttribute("value", "3");
    // option_3.innerHTML='3';
    //
    // let option_4 = document.createElement("option");
    // option_4.setAttribute("value", "4");
    // option_4.innerHTML='4';
    //
    // let option_5 = document.createElement("option");
    // option_1.setAttribute("value", "5");
    // option_5.innerHTML='5';
    //
    //
    //
    // let detail_001 = document.createElement("textarea");
    // detail_001.setAttribute("name","board_content");
    // detail_001.setAttribute("class", "detail_001");
    // detail_001.setAttribute("placeholder", "사진에대해 설명해 주세요");
    // detail_001.setAttribute("row", "6");
    // detail_001.style.overflow = "hidden";
    // detail_001.style.overflowWrap = "break-word";
    // detail_001.style.height = "145px";
    //
    // let keyword_001 = document.createElement("div");
    // keyword_001.setAttribute("class", "keyword_001");
    //
    // let keyword_002 = document.createElement("div");
    // keyword_002.setAttribute("class", "keyword_002");
    // keyword_002.style.display = "inline-block";
    //
    // let keyword_main = document.createElement("input");
    // keyword_main.setAttribute("class", "keyword_main");
    // keyword_main.setAttribute("placeholder", "#태그");
    // keyword_main.setAttribute("name","hashTag")
    // keyword_main.style.boxSizing = "content-box";
    // keyword_main.style.width = "60px";
    //
    // let append_per = document.getElementById("main_001");
    //
    //
    // append_per.appendChild(warp_main);
    // warp_main.appendChild(main_005);
    // warp_main.appendChild(ol_001).appendChild(li_001);
    // warp_main.appendChild(ol_002).appendChild(li_002);
    // li_002.appendChild(warp_001).appendChild(warp_002);
    // warp_001.appendChild(postion_sapn).appendChild(postion_title).appendChild(postion_addr);
    // warp_002.appendChild(photo_001).appendChild(photo_btn).appendChild(photo_002).appendChild(img_tag);
    // warp_002.appendChild(write_001).appendChild(write_002);
    // write_002.appendChild(title_001);
    // title_001.appendChild(span_tag).appendChild(text_h4).appendChild(text_tag);
    // title_001.appendChild(select_span).appendChild(select_star).appendChild(option_main);
    // select_star.appendChild(option_1);
    // select_star.appendChild(option_2);
    // select_star.appendChild(option_3);
    // select_star.appendChild(option_4);
    // select_star.appendChild(option_5);
    // write_002.appendChild(detail_001);
    // write_002.appendChild(keyword_001).appendChild(keyword_002).appendChild(keyword_main);

    return count;
}




