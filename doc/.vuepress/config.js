module.exports = {
  title: 'VISIT KOREA',
  themeConfig: {
    nav: [
      { text: 'TODO', link: '/todo/' },
      { text: 'DONE', link: '/done/' },
    ],
    sidebar: {
      '/todo/': [
        'upload_excel_image',
        'tour_api_batch',
      ],
      '/done/': [
        'city_tour_excel_image',
        'no_pain_tour_distinct_detail_info',
        // 'response-1',
        // 'response-2',
        // 'response-3',
      ]
    }
  },
}